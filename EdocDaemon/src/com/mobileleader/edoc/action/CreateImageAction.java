package com.mobileleader.edoc.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.inzisoft.pdf2image.InziPDF;
import com.mobileleader.edoc.daemon.EdsTask;
import com.mobileleader.edoc.daemon.EdsTaskWorkerResult;
import com.mobileleader.edoc.data.dao.TbEdsElecDocFileProcsMgmtVo;
import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupBzwkInfoVo;
import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsMgmtVo;
import com.mobileleader.edoc.exception.EdocError;
import com.mobileleader.edoc.exception.EdocException;
import com.mobileleader.edoc.type.ProcessStatusCode;
import com.mobileleader.edoc.type.ProcessStepCode;
import com.mobileleader.edoc.util.DateUtil;
import com.mobileleader.edoc.util.FileUtil;
import com.mobileleader.edoc.util.StringUtil;

/**
 * PDF 파일을 이미지로 변환 및 병합 처리 액션
 */
public class CreateImageAction extends AbstractEdocAction {

	private Logger logger = LoggerFactory.getLogger(CreateImageAction.class);

	@Value("${DATA_HOME_PATH")
	private String DATA_HOME_PATH;

	@Value("${TSAPDF_FILE_PATH}")
	private String TSAPDF_FILE_PATH;

	@Value("${IMG_FILE_PATH}")
	private String IMG_FILE_PATH;

	private int comprate = 80;

	int delimiter = '|';

	@Override
	public EdsTaskWorkerResult execute(EdsTask task) throws Exception {

		TbEdsElecDocGroupProcsMgmtVo edocProcess = task.getTask();
		EdsTaskWorkerResult result = new EdsTaskWorkerResult();

		String edocIndexNo = edocProcess.getElecDocGroupInexNo();
		
		List<TbEdsElecDocFileProcsMgmtVo> fileList = null;
		TbEdsElecDocGroupBzwkInfoVo edocBiz = null;

		int retP2I = 0;
		
		try {
			// 처리 상태 변경 - 처리중
			edocProcess.setProcsStepCd(ProcessStepCode.PDF_CONVERT.getCode());
			edocProcess.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
			edocProcess.setProcsStepStTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);
						
			// 파일목록 및 업무 조회
			fileList = getFileProcessList(edocIndexNo);
			edocBiz = getProcessBizInfo(edocIndexNo);

			// 경로
			String defaultPath = DATA_HOME_PATH + edocProcess.getCrtnTimeString("yyyyMMdd") + File.separator
					+ edocBiz.getDsrbCd() + File.separator + edocProcess.getElecDocGroupInexNo();
			String tsaDir = defaultPath + File.separator + TSAPDF_FILE_PATH;
			String imgDir = null;
			
			List<File> removeFiles = new ArrayList<File>();

			for (int i = 0; i < fileList.size(); i++) {
				TbEdsElecDocFileProcsMgmtVo file = fileList.get(i);

				// image save directory path
				imgDir = defaultPath + File.separator + IMG_FILE_PATH + file.getFileSeqNo();
				FileUtil.makeFolder(imgDir);

				String imgPath = imgDir + File.separator + FilenameUtils.getBaseName(file.getPdfFileNm()) + ".tif";

				File imgFile = new File(imgPath);
	
				// 이미 처리한 파일은 skip
				if (ProcessStepCode.PDF_CONVERT.getCode().equals(file.getProcsStepCd())
						&& ProcessStatusCode.SUCCESS.getCode().equals(file.getProcsStepStcd())
						&& imgFile.exists()) {
					continue;
				}
				
				// file 상태 변경 - 처리중
				file.setProcsStepCd(edocProcess.getProcsStepCd());
				file.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
				updateFileProcessStatus(file);

				// Convert
				retP2I = convertPDF2Image(file, tsaDir, imgDir, result);
				
				String prefix = imgDir + File.separator + FilenameUtils.getBaseName(file.getPdfFileNm());
				String mergeFileName = prefix + ".tif";
				String mergeFileName_enc = prefix + ".tif_enc";
				removeFiles.clear();

				// 원본이미지가 있을 경우 삭제
				File mergeFile = new File(mergeFileName);
				File mergeFile_enc = new File(mergeFileName_enc);
				if (mergeFile.exists()) {
					FileUtil.fileDelete(mergeFile);
				}
				if (mergeFile_enc.exists()) {
					FileUtil.fileDelete(mergeFile_enc);
				}

				File dir = new File(imgDir);
				File[] tmpImgFiles = dir.listFiles();
				Arrays.sort(tmpImgFiles);

				String singleTIFFList = getSingleTiffList(edocIndexNo, tmpImgFiles, prefix, retP2I, removeFiles);
				logger.info("singleTIFFList=" + singleTIFFList);
				logger.info("delimiter=" + StringUtil.asciiToString(delimiter));
				logger.info("mergeFileName=" + mergeFileName);

				// Merge
				mergeTIFF(file, singleTIFFList, mergeFileName, result);

				// 임시이미지파일 삭제
				removeTmpImgFiles(edocIndexNo, removeFiles);
			}

			// work 상태 변경 - 완료
			edocProcess.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
			edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);

			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);

		} catch (Exception e) {
			logger.error("PDF -> IMG Convert Error - {}", edocIndexNo);
			
			try {
				// work 상태 변경 - 에러
				edocProcess.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
				updateProcessStatus(edocProcess);
			} catch (Exception e1) {
				throw new EdocException("Update Process Status Error", EdocError.SQL_ERROR);
			}
			
			if (result.getStatusCode() == null) {
				result.setStatusCode(EdocError.SQL_ERROR.getCode());
				result.setStatusMsg(EdocError.SQL_ERROR.getMessage());
			}

			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);

			return result;
		}

		return result;
	}

	/**
	 * 싱글Tiff목록조회
	 *
	 * @param File   [] files 디렉토리파일목록
	 * @param String prefix PreFix
	 * @param int    fileCnt 파일수
	 * @param List   <File> removeFiles 제거대상파일목록
	 * @return void
	 *
	 * @throws EdsException
	 */
	private String getSingleTiffList(String edocIndexNo, File[] files, String prefix, int fileCnt,
			List<File> removeFiles) throws Exception {

		logger.debug("start [elecDocGroupInexNo : " + edocIndexNo + "]");

		StringBuilder sb = new StringBuilder();

		try {
			int pageCnt = 0;
			for (int i = 0; i < files.length; i++) {
				if ((prefix.length() <= files[i].getAbsolutePath().length())
						&& prefix.equals(files[i].getAbsolutePath().substring(0, prefix.length()))) {
					sb.append(files[i].getAbsolutePath());
					sb.append(StringUtil.asciiToString(delimiter));
					removeFiles.add(files[i]);
					pageCnt++;
				}
			}
			if (pageCnt != fileCnt) {
				logger.error("Image Merge Page Count Error [fileCnt=" + fileCnt + ",pageCnt=" + pageCnt + "]");
				throw new EdocException("Image Merge Page Count Error : fileCnt : " + fileCnt + ", pageCnt : " + pageCnt, 
						EdocError.IMG_PAGE_COUNT_ERROR);
			}
		} catch (Exception e) {
			logger.error("error [elecDocGroupInexNo : " + edocIndexNo + "]", e);
			throw e;
		} 
		
		return sb.toString();
	}

	/**
	 * 임시이미지파일 삭제
	 *
	 * @param List <File> removeFiles 제거대상파일목록
	 * @return void
	 *
	 * @throws EdsException
	 */
	private void removeTmpImgFiles(String edocIndexNo, List<File> removeFiles) throws Exception {

		logger.debug("start [elecDocGroupInexNo : " + edocIndexNo + "]");

		try {
			for (int i = 0; i < removeFiles.size(); i++) {
				logger.debug("remove File : " + removeFiles.get(i).getName());
				removeFiles.get(i).delete();
			}
			logger.info("remove all temp image file [elecDocGroupInexNo : " + edocIndexNo);
		} catch (Exception e) {
			logger.error("error [elecDocGroupInexNo : " + edocIndexNo + "]", e);
			throw e;
		} finally {
			logger.debug("end [elecDocGroupInexNo : " + edocIndexNo + "]");
		}
	}

	private int convertPDF2Image(TbEdsElecDocFileProcsMgmtVo file, String inPath, String outPath,
			EdsTaskWorkerResult result) throws Exception {
		logger.debug("InziPDF.convertPDF2Image() start [elecDocGroupInexNo : " + file.getElecDocGroupInexNo()
				+ ",fileSeqNo : " + file.getFileSeqNo() + "]");

		int retP2I = -999;
		logger.debug("inPath : " + inPath + file.getFileSeqNo() + File.separator + file.getPdfFileNm());
		logger.debug("outPath : " + outPath);
		
		try {
			// JPEG2000
			retP2I = InziPDF.convertPDF2Image(inPath + file.getFileSeqNo() + File.separator + file.getPdfFileNm(),
					outPath, 200, comprate, 4, 34713, 0, 0);
		} catch (Exception e) {
			logger.error("InziPDF.convertPDF2Image() error [elecDocGroupInexNo : " + file.getElecDocGroupInexNo()
					+ ",fileSeqNo : " + file.getFileSeqNo() + "]", e);

			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusCode(EdocError.IMG_CONVERT_MODULE_ERROR.getCode());
			result.setStatusMsg(EdocError.IMG_CONVERT_MODULE_ERROR.getMessage());
			throw new EdocException(e.getMessage(), EdocError.IMG_CONVERT_MODULE_ERROR);
		}

		if (retP2I != Integer.parseInt(String.valueOf(file.getPageCnt()))) {
			String errStr = "InziPDF.convertPDF2Image() error [elecDocGroupInexNo : " + file.getElecDocGroupInexNo()
					+ ",fileSeqNo : " + file.getFileSeqNo() + "] ReturnCode : " + retP2I;
			logger.error(errStr);

			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusCode(EdocError.IMG_CONVERT_MODULE_ERROR.getCode());
			result.setStatusMsg(EdocError.IMG_CONVERT_MODULE_ERROR.getMessage());
			throw new EdocException("retP2I = " + retP2I, EdocError.IMG_CONVERT_MODULE_ERROR);
		}

		logger.debug("InziPDF.convertPDF2Image() end [elecDocGroupInexNo : " + file.getElecDocGroupInexNo()
				+ ",fileSeqNo : " + file.getFileSeqNo() + "]");
		
		return retP2I;
	}

	private void mergeTIFF(TbEdsElecDocFileProcsMgmtVo file, String singleTIFFList, String mergeFileName,
			EdsTaskWorkerResult result) throws Exception {
		logger.debug("InziPDF.mergeTIFF() start [elecDocGroupInexNo : " + file.getElecDocGroupInexNo() + ",fileSeqNo : "
				+ file.getFileSeqNo() + "]");
		int retMerge = -999;
		try {
			retMerge = InziPDF.mergeTIFF(singleTIFFList, delimiter, mergeFileName);
		} catch (Exception e) {
			logger.error("InziPDF.mergeTIFF error [elecDocGroupInexNo : " + file.getElecDocGroupInexNo()
					+ ",fileSeqNo : " + file.getFileSeqNo() + "] resultCode : " + String.valueOf(retMerge), e);

			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusCode(EdocError.IMG_MERGE_MODULE_ERROR.getCode());
			result.setStatusMsg(EdocError.IMG_MERGE_MODULE_ERROR.getMessage());
			
			throw new EdocException(e.getMessage(), EdocError.IMG_MERGE_MODULE_ERROR);
		}

		if (retMerge != 0) {
			logger.error("InziPDF.mergeTIFF error [elecDocGroupInexNo : " + file.getElecDocGroupInexNo()
					+ ",fileSeqNo : " + file.getFileSeqNo() + "] ReturnCode : " + retMerge);
			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusCode(EdocError.IMG_MERGE_MODULE_ERROR.getCode());
			result.setStatusMsg(EdocError.IMG_MERGE_MODULE_ERROR.getMessage());
			
			throw new EdocException("retMerge = " + retMerge, EdocError.IMG_MERGE_MODULE_ERROR);
		}
		
		// file 상태 변경 - 처리완료
		file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
		updateFileProcessStatus(file);

		logger.debug("InziPDF.mergeTIFF() end [elecDocGroupInexNo : " + file.getElecDocGroupInexNo() + ",fileSeqNo : "
				+ file.getFileSeqNo() + "]");
	}

}
