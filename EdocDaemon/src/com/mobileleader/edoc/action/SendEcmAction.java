package com.mobileleader.edoc.action;

import java.io.File;
import java.net.InetAddress;
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
import com.mobileleader.edoc.type.DocType;
import com.mobileleader.edoc.type.ProcessStatusCode;
import com.mobileleader.edoc.type.ProcessStepCode;
import com.mobileleader.edoc.util.DateUtil;
import com.mobileleader.image.client.service.UploadService;
import com.mobileleader.image.db.dto.ImageFileDto;
import com.mobileleader.image.db.dto.ImageStoreDto;
import com.mobileleader.image.server.model.request.UploadRequest;
import com.mobileleader.image.server.model.response.UploadResponse;

/**
 * ECM 전송 액션
 */
@Component("sendEcmAction")
public class SendEcmAction extends AbstractEdocAction {

	private Logger logger = LoggerFactory.getLogger(AbstractEdocAction.class);

	@Value("${DATA_HOME_PATH}")
	private String DATA_HOME_PATH;

	@Value("${TSAPDF_FILE_PATH}")
	private String TSAPDF_FILE_PATH;

	@Value("${XML_FILE_PATH}")
	private String XML_FILE_PATH;

	@Value("${ECM_URL_INFO}")
	private String ECM_URL_INFO;

	@Override
	public EdsTaskWorkerResult execute(EdsTask task) throws EdocException {

		TbEdsElecDocGroupProcsMgmtVo edocProcess = task.getTask();
		EdsTaskWorkerResult result = new EdsTaskWorkerResult();

		String edocIndexNo = edocProcess.getElecDocGroupInexNo();

		List<TbEdsElecDocFileProcsMgmtVo> fileList = null;

		TbEdsElecDocGroupBzwkInfoVo edocBiz = null;

		try {
			// work 상태 변경 - 처리중
			edocProcess.setProcsStepCd(ProcessStepCode.ECM.getCode());
			edocProcess.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
			edocProcess.setProcsStepStTime(DateUtil.getCurrentTimeStamp());
			updateProcessStatus(edocProcess);

			// 파일목록 및 업무 조회
			fileList = getFileProcessList(edocIndexNo);
			edocBiz = getProcessBizInfo(edocIndexNo);

			String defaultPath = DATA_HOME_PATH + edocProcess.getCrtnTimeString("yyyyMMdd") + File.separator
					+ edocBiz.getDsrbCd() + File.separator + edocProcess.getElecDocGroupInexNo();
			
			UploadResponse response = sendToEcm(edocBiz, fileList, defaultPath, result);
			
			// 서버 응답처리.(파일이 하나라도 등록이 안되면 오류 응답, 정상응답 시 파일 일괄 상태 업데이트)
			if (response.getResultCode() == 200) {

				for (TbEdsElecDocFileProcsMgmtVo file : fileList) {
					// file 상태 변경 - 처리완료
					file.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
					file.setProcsStepCd(ProcessStepCode.ECM.getCode());
					updateFileProcessStatus(file);
				}

				// work 상태 변경 - 완료
				edocProcess.setProcsStepStcd(ProcessStatusCode.SUCCESS.getCode());
				edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
				updateProcessStatus(edocProcess);

				result.setBzwkInfo(edocBiz);
				result.setResult(edocProcess);
			} else {
				// work 상태 변경 - 에러
				edocProcess.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
				updateProcessStatus(edocProcess);
				
				for (TbEdsElecDocFileProcsMgmtVo file : fileList) {
					// file 상태 변경 - 처리에러
					file.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
					updateFileProcessStatus(file);
				}

				String errStr = "ECM Send Fail [" + edocProcess.getElecDocGroupInexNo() + ","
						+ response.getResultMessage() + "]";
				logger.error(errStr);
				
				result.setStatusCode(EdocError.ECM_SEND_ERROR.getCode());
				result.setStatusMsg(response.getResultMessage());
				
				throw new EdocException(response.getResultMessage(), EdocError.ECM_SEND_ERROR);
			}
		} catch (Exception e) {
			logger.error("ECM Send Exception [" + edocProcess.getElecDocGroupInexNo() + "]", e);

			try {
				// work 상태 변경 - 에러
				edocProcess.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				edocProcess.setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
				updateProcessStatus(edocProcess);
			} catch (Exception e1) {
				throw new EdocException("Update Process Status Error", EdocError.SQL_ERROR);
			}

			if (result.getStatusCode() == null) {
				result.setStatusCode(EdocError.ECM_SEND_ERROR.getCode());
				result.setStatusMsg(EdocError.ECM_SEND_ERROR.getMessage());
			}
			
			result.setBzwkInfo(edocBiz);
			result.setResult(edocProcess);
			return result;
		} 
		
		return result;
	}

	private UploadResponse sendToEcm(TbEdsElecDocGroupBzwkInfoVo edocBiz, List<TbEdsElecDocFileProcsMgmtVo> files,
			String defaultPath, EdsTaskWorkerResult result) throws Exception {
		
		// 이미지 서버 Request 생성
		UploadRequest uploadRequest = new UploadRequest();
		
		ImageStoreDto imageStoreDto = new ImageStoreDto()		
				.setMainKey(edocBiz.getMainKey())
				.setBranchCode(edocBiz.getDsrbCd())
				.setBranchTitle(edocBiz.getDsrbNm())
				.setEmployeeId(edocBiz.getHndrNo())
				.setEmployeeName(edocBiz.getHndrNm())
				.setDocMappingCount(files.size())
				.setPreviousDeviceInfo(InetAddress.getLocalHost().getHostAddress())
				
				// 업무(이미지) 정보
				.setInsourceId(edocBiz.getInsourceId())
				.setInsourceTitle(edocBiz.getInsourceTitle())
				.setCustomerName(edocBiz.getCustomerName())
				.setTaskKey(edocBiz.getTaskKey())
				.setMemo(edocBiz.getMemo());
		
		uploadRequest.setImageStoreDto(imageStoreDto);

		ImageFileDto imageFileDto = null;

		// PDF 파일 전송
		for (int j = 0; j < files.size(); j++) {

			TbEdsElecDocFileProcsMgmtVo pdfFile = files.get(j);
			
			// 이미 처리한 파일은 skip
			if (ProcessStepCode.ECM.getCode().equals(pdfFile.getProcsStepCd())
					&& ProcessStatusCode.SUCCESS.getCode().equals(pdfFile.getProcsStepStcd())) {
				continue;
			}

			// 파일 처리상태 변경 - 처리중
			pdfFile.setProcsStepCd(ProcessStepCode.PDF_CREATE.getCode());
			pdfFile.setProcsStepStcd(ProcessStatusCode.ONGOING.getCode());
			updateFileProcessStatus(pdfFile);
			
			String pdfDir = defaultPath + File.separator + TSAPDF_FILE_PATH + pdfFile.getFileSeqNo()
					+ File.separator;

			String pdfPath = pdfDir + pdfFile.getPdfFileNm();

			File imgFile = new File(pdfPath);

			if (!imgFile.exists()) {
				result.setStatusCode(EdocError.ECM_FILE_NOT_FOUND.getCode());
				result.setStatusMsg(EdocError.ECM_FILE_NOT_FOUND.getMessage());

				throw new EdocException(pdfFile.getPdfFileNm() + " Not Found", EdocError.ECM_FILE_NOT_FOUND);
			}

			imageFileDto = new ImageFileDto()
					.setDocId(pdfFile.getLefrmCd())
					.setDocTitle(pdfFile.getLefrmNm())
					.setDocType(DocType.PDF.getType())
					.setFileName(pdfPath)
					.setPageCnt(pdfFile.getPageCnt())
					.setFunnels(DocType.PDF.getChannel())
					.setFileOrder((int) pdfFile.getFileSeqNo()) 
					.setVersionInfo(1);

			uploadRequest.setImageFileDtos(imageFileDto);
		}

		// ECM전송
		UploadService ecmService = new UploadService();

		logger.info("uploadRequest : " + uploadRequest.toString());

		UploadResponse response = ecmService.upload(ECM_URL_INFO, uploadRequest);
		logger.info("uploadResponse - resultCode : {}, resultMessage : {}", response.getResultCode(), response.getResultMessage());
		
		return response;
	}
	
}
