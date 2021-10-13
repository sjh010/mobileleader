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

import com.inzisoft.server.pdf.PDFEditor;
import com.inzisoft.server.pdf.PDFEditorJNI;
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
 * 전자서식 + XML 파일을 PDF로 변환 처리 액션
 */
@Component("createPdfAction")
public class CreatePdfAction extends AbstractEdocAction {

	private Logger logger = LoggerFactory.getLogger(CreatePdfAction.class);

	@Value("${DATA_HOME_PATH}")
	private String DATA_HOME_PATH;

	@Value("${XML_FILE_PATH}")
	private String XML_FILE_PATH;

	@Value("${PDF_FILE_PATH}")
	private String PDF_FILE_PATH;

	@Value("${INZI_PATH}")
	private String INZI_PATH;

	@Value("${MASTER_FORM_PATH}")
	private String MASTER_FORM_PATH;

	@Override
	public EdsTaskWorkerResult execute(EdsTask task) throws EdocException {

		TbEdsElecDocGroupProcsMgmtVo edocProcess = task.getTask();
		EdsTaskWorkerResult result = new EdsTaskWorkerResult();

		String edocIndexNo = edocProcess.getElecDocGroupInexNo();

		List<TbEdsElecDocFileProcsMgmtVo> fileList = null;
		TbEdsElecDocGroupBzwkInfoVo edocBiz = null;

		try {
			// 처리 상태 변경 - 진행중
			edocProcess.setProcsStepCd(ProcessStepCode.PDF_CREATE.getCode());
			edocProcess.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
			edocProcess.setProcsStepStTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);

			// 파일목록 및 업무 조회
			fileList = getFileProcessList(edocIndexNo);
			edocBiz = getProcessBizInfo(edocIndexNo);

			// 경로
			String defaultDir = DATA_HOME_PATH + edocProcess.getCrtnTimeString("yyyyMMdd") + File.separator
					+ edocBiz.getDsrbCd() + File.separator + edocProcess.getElecDocGroupInexNo();
			String xmlDir = defaultDir + File.separator + XML_FILE_PATH;
			String pdfDir = defaultDir + File.separator + PDF_FILE_PATH;

			for (int i = 0; i < fileList.size(); i++) {
				TbEdsElecDocFileProcsMgmtVo file = fileList.get(i);

				// 이미 처리한 파일은 skip
				if (ProcessStepCode.PDF_CREATE.getCode().equals(file.getProcsStepCd())
						&& ProcessStatusCode.SUCCESS.getCode().equals(file.getProcsStepStcd())) {
					continue;
				}
				
				// 파일 처리상태 변경 - 진행중
				file.setProcsStepCd(ProcessStepCode.PDF_CREATE.getCode());
				file.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
				updateFileProcessStatus(file);

				// 생성할 PDF 파일명 세팅
				StringBuilder pdfFileName = new StringBuilder();
				pdfFileName.append(file.getLefrmCd());
				pdfFileName.append("_");
				pdfFileName.append(StringUtil.padLeft(String.valueOf(file.getFileSeqNo()), '0', 3));
				pdfFileName.append(".pdf");

				file.setPdfFileNm(pdfFileName.toString());				

				// PDF 저장 디렉토리 생성
				FileUtil.makeFolder(pdfDir + file.getFileSeqNo());

				// PDF 생성
				makePdfFile(xmlDir, pdfDir, file, result);

				// PDF 페이지 수
				String resultPDFPath = pdfDir + file.getFileSeqNo() + File.separator + file.getPdfFileNm();
				int pageCount = getPdfPageCount(resultPDFPath);

				// file 상태 변경 - 처리완료
				file.setPageCnt(pageCount);
				file.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
				updateFileProcessStatus(file);
			}
			// work 상태 변경 - 완료
			edocProcess.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
			edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);

			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);
		} catch (Exception e) {			
			logger.error("PDF Create Error - {}", edocIndexNo);

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
				result.setStatusCode(EdocError.PDF_CREATE_ERROR.getCode());
				result.setStatusMsg(EdocError.PDF_CREATE_ERROR.getMessage());
			}

			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);

			return result;
		}

		return result;
	}

	private void makePdfFile(String xmlDir, String pdfDir, TbEdsElecDocFileProcsMgmtVo file, EdsTaskWorkerResult result)
			throws Exception {

		String defaultLog = "[" + file.getElecDocGroupInexNo() + " - " + file.getLefrmCd() + "]";

		int retMakePDF = -999;
		String makexmlPath = xmlDir + String.valueOf(file.getFileSeqNo()) + File.separator + file.getXmlFileNm();
		String resultPDFPath = pdfDir + file.getFileSeqNo() + File.separator + file.getPdfFileNm();

		// 서식 XML 파일 존재 체크
		checkXmlFile(makexmlPath, result);
		
		PDFEditor pdfEditor = new PDFEditor(INZI_PATH, xmlDir + String.valueOf(file.getFileSeqNo()));

		try {
			retMakePDF = pdfEditor.Make(MASTER_FORM_PATH, makexmlPath, resultPDFPath);
		} catch (Exception e) {
			result.setStatusCode(EdocError.PDF_CREATE_MODULE_ERROR.getCode());
			result.setStatusMsg(e.getMessage());
			
			throw new EdocException("PDF File Make Error", EdocError.PDF_CREATE_MODULE_ERROR);
		}
		
		if (retMakePDF == 0) {
			logger.info("PDF Make Success " + defaultLog);
		} else {
			String errlog = "PDF Create Module Error " + defaultLog + " ReturnCode : " + retMakePDF;

			logger.error(errlog);

			// file 상태 변경 - 처리에러
			file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			updateFileProcessStatus(file);

			result.setStatusCode(EdocError.PDF_CREATE_MODULE_ERROR.getCode());
			result.setStatusMsg(EdocError.PDF_CREATE_MODULE_ERROR.getMessage());

			throw new EdocException(errlog, EdocError.PDF_CREATE_MODULE_ERROR);
		}
	}

	private int getPdfPageCount(String resultPDFPath) throws Exception {
		int openDocIdx = PDFEditorJNI.openDocument(resultPDFPath);
		int pageCount = PDFEditorJNI.countPages(openDocIdx);

		PDFEditorJNI.closeDocument(openDocIdx);

		return pageCount;
	}
	
	private void checkXmlFile(String xmlFilePath, EdsTaskWorkerResult result) {
		
		File xmlFile = new File(xmlFilePath);
		
		InputStream in = null;

		try {
			in = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			logger.error("XML File Not Found", e.getMessage());
			result.setStatusCode(EdocError.XML_FILE_NOT_FOUND.getCode());
			result.setStatusMsg(EdocError.XML_FILE_NOT_FOUND.getMessage());
			
			throw new EdocException("XML File Not Found", EdocError.XML_FILE_NOT_FOUND);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error("InputStream Close Exception", e);
			}
		}
	}
}
