package com.mobileleader.edoc.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import com.mobileleader.tsa.common.PropertiesConfig;
import com.mobileleader.tsa.common.TSACommonException;
import com.mobileleader.tsa.pdfsign.PdfSigner;
import com.mobileleader.tsa.pdfsign.PdfSignerException;
import com.mobileleader.tsa.pdfsign.PdfSignerImpl;
import com.mobileleader.tsa.pdfsign.PdfSignerParameter;

/**
 * TSA처리된 PDF생성 액션
 */
@Component("createTsaAction")
public class CreateTsaAction extends AbstractEdocAction {

	private Logger logger = LoggerFactory.getLogger(CreateTsaAction.class);

	@Value("${DATA_HOME_PATH}")
	private String DATA_HOME_PATH;

	@Value("${PDF_FILE_PATH}")
	private String PDF_FILE_PATH;

	@Value("${TSAPDF_FILE_PATH}")
	private String TSAPDF_FILE_PATH;

	@Value("${TSA_CONF_PATH}")
	private String TSA_CONF_PATH;

	@Override
	public EdsTaskWorkerResult execute(EdsTask task) throws EdocException {

		TbEdsElecDocGroupProcsMgmtVo edocProcess = task.getTask();
		EdsTaskWorkerResult result = new EdsTaskWorkerResult();

		String edocIndexNo = edocProcess.getElecDocGroupInexNo();

		List<TbEdsElecDocFileProcsMgmtVo> fileList = null;
		TbEdsElecDocGroupBzwkInfoVo edocBiz = null;

		try {
			// work 상태 변경 - 처리중
			edocProcess.setProcsStepCd(ProcessStepCode.TSA.getCode());
			edocProcess.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
			edocProcess.setProcsStepStTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);

			// 파일목록 및 업무 조회
			fileList = getFileProcessList(edocIndexNo);
			edocBiz = getProcessBizInfo(edocIndexNo);

			// 경로
			String defaultPath = DATA_HOME_PATH + edocProcess.getCrtnTimeString("yyyyMMdd") + File.separator
					+ edocBiz.getDsrbCd() + File.separator + edocProcess.getElecDocGroupInexNo();
			String pdfDir = defaultPath + File.separator + PDF_FILE_PATH;
			String tsaDir = defaultPath + File.separator + TSAPDF_FILE_PATH;

			for (int i = 0; i < fileList.size(); i++) {
				TbEdsElecDocFileProcsMgmtVo file = fileList.get(i);

				// 이미 처리한 파일은 skip
				if (ProcessStepCode.TSA.getCode().equals(file.getProcsStepCd())
						&& ProcessStatusCode.SUCCESS.getCode().equals(file.getProcsStepStcd())) {
					continue;
				}

				// file 상태 변경 - 진행중
				file.setProcsStepCd(ProcessStepCode.TSA.getCode());
				file.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
				updateFileProcessStatus(file);

				// TAS PDF 저장 디렉토리 생성
				FileUtil.makeFolder(tsaDir + file.getFileSeqNo());

				// PDF Sign
				PDFSigner(pdfDir, tsaDir, file, result);
			}

			// work 상태 변경 - 완료
			edocProcess.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
			edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);

			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);

		} catch (Exception e) {
			logger.error("TSA PDF Create Error - {}", edocIndexNo);

			try {
				// work 상태 변경 - 에러
				edocProcess.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
				updateProcessStatus(edocProcess);
			} catch (Exception e1) {
				throw new EdocException("Update Process Status Error", EdocError.SQL_ERROR);
			}

			// 결과 값 리턴
			if (result.getStatusCode() == null) {
				result.setStatusCode(EdocError.TSA_CREATE_ERROR.getCode());
				result.setStatusMsg(EdocError.TSA_CREATE_ERROR.getMessage());
			}

			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);

			return result;
		}
		return result;
	}

	private void PDFSigner(String inPath, String outPath, TbEdsElecDocFileProcsMgmtVo file, EdsTaskWorkerResult result)
			throws Exception {

		String fileLog = "[" + file.getElecDocGroupInexNo() + " - " + file.getLefrmCd() + "]";

		String pdfPath = inPath + File.separator + file.getFileSeqNo() + File.separator + file.getPdfFileNm();
		String outPdfPath = outPath + File.separator + file.getFileSeqNo() + File.separator + file.getPdfFileNm();

		// PDF 파일 존재 유무 체크
		checkPdfFile(pdfPath, result);

		PropertiesConfig config = new PropertiesConfig();

		try {
			config.load(TSA_CONF_PATH);
		} catch (TSACommonException e) {
			logger.error("TSA Config load failed", e);
			throw new EdocException("TSA Config load failed", EdocError.TSA_CONFIG_FILE_LOAD_ERROR);
		}

		try {
			PdfSignerParameter param = new PdfSignerParameter(config);

			PdfSigner pdfSigner = new PdfSignerImpl(param);
			logger.debug("Pdf signer initialized");

			pdfSigner.signPdf(pdfPath, outPdfPath);

			File outPdfFile = new File(outPdfPath);

			if (outPdfFile.exists() && outPdfFile.getName().equals(file.getPdfFileNm())) {
				logger.info("TSA Success {}", fileLog);
				
				// file 상태 변경 - 처리완료
				file.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
				updateFileProcessStatus(file);
			} else {
				// file 상태 변경 - 처리에러
				file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				updateFileProcessStatus(file);
				
				String errorLog = "TSA Fail " + fileLog;
				logger.error(errorLog);
				throw new EdocException(errorLog, EdocError.TSA_CREATE_MODULE_ERROR);
			}
		} catch (PdfSignerException e) {
			logger.error("TSA Sign failed : PdfSignerException", e);

			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusMsg(e.getMessage());
			result.setStatusCode(EdocError.TSA_CREATE_MODULE_ERROR.getCode());

			throw e;
		} catch (Exception e) {
			logger.error("TSA Sign failed : Exception", e);

			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusMsg(e.getMessage());
			result.setStatusCode(EdocError.TSA_CREATE_MODULE_ERROR.getCode());

			throw e;
		}
	}

	private void checkPdfFile(String pdfFilePath, EdsTaskWorkerResult result) {

		File pdfFile = new File(pdfFilePath);

		InputStream in = null;

		try {
			in = new FileInputStream(pdfFile);
		} catch (FileNotFoundException e) {
			logger.error("PDF File Not Found", e);
			result.setStatusCode(EdocError.PDF_FILE_NOT_FOUND.getCode());
			result.setStatusMsg(EdocError.PDF_FILE_NOT_FOUND.getMessage());

			throw new EdocException("PDF File Not Found", EdocError.PDF_FILE_NOT_FOUND);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error("InputStream Close Exception", e);
			}
		}
	}

}
