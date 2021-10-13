package com.mobileleader.edoc.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mobileleader.edoc.action.EdocAction;
import com.mobileleader.edoc.exception.EdocError;
import com.mobileleader.edoc.exception.EdocException;
import com.mobileleader.edoc.type.ProcessMessageCode;
import com.mobileleader.edoc.type.ProcessStatusCode;
import com.mobileleader.edoc.type.ProcessStepCode;
import com.mobileleader.edoc.util.DateUtil;

/**
 * EdsTaskWorker 구현 클래스
 * */
@Component
public class EdsTaskWorkerImpl implements EdsTaskWorker {

	private Logger logger = LoggerFactory.getLogger(EdsTaskWorkerImpl.class);
	
	@Value("${DATA_HOME_PATH}")
	private String DATA_HOME_PATH;
	
	@Value("${XML_FILE_PATH}")
	private String XML_FILE_PATH;
	
	@Value("${PDF_FILE_PATH}")
	private String PDF_FILE_PATH;
	
	@Value("${TSAPDF_FILE_PATH}")
	private String TSAPDF_FILE_PATH;
	
	@Value("${IMG_FILE_PATH}")
	private String IMG_FILE_PATH;
	
	@Value("${TEMP_FILE_PATH}")
	private String TEMP_FILE_PATH;
	
	@Autowired
	@Qualifier("createPdfAction")
	private EdocAction createPdfAction;
	
	@Autowired
	@Qualifier("createTsaAction")
	private EdocAction createTsaAction;
	
	@Autowired
	@Qualifier("sendEcmAction")
	private EdocAction sendEcmAction;

	@Override
	@Transactional
	public EdsTaskWorkerResult work(EdsTask task) {
		
		EdsTaskWorkerResult result = new EdsTaskWorkerResult();

		String elecDocGroupInexNo = task.getTask().getElecDocGroupInexNo();
	
		try {
			String currentProcessStepCode = task.getTask().getProcsStepCd();
			String currentProcessMessageCode = task.getTask().getProcsStepMsgCd();
			String currentProcessStatusCode = task.getTask().getProcsStepStcd();
			
			String nextProcessStepCode = "";

			// 처리 상태 코드가 '성공' 이고, 처리 메세지 코드가 '취소처리대상'이 아닌 경우 다음 단계 진행
			if (ProcessStatusCode.SUCCESS.getCode().equals(currentProcessStatusCode)
					&& !ProcessMessageCode.CANCEL.getCode().equals(currentProcessMessageCode)) {
				nextProcessStepCode = ProcessStepCode.getNextCode(currentProcessStepCode).getCode();
			} 
			// 처리 단계가 '취소거래' 이고, 처리 메시지 코드가 '취소처리대상'인 경우, 현재 단계 진행
			else if (ProcessMessageCode.CANCEL.getCode().equals(currentProcessMessageCode)
					&& ProcessStepCode.CANCEL.getCode().equals(currentProcessStepCode)) {
				nextProcessStepCode = currentProcessStepCode;
			} 
			// 처리 상태 코드가 '진행중' 또는 '실패'인 경우, 현재 단계 진행
			else if (ProcessStatusCode.ONGOING.getCode().equals(currentProcessStatusCode) || 
					ProcessStatusCode.FAIL.getCode().equals(currentProcessStatusCode)) {
				nextProcessStepCode = currentProcessStepCode;
			} 
			// 위의 3가지 경우가 아닌 경우, 익셉션 발생
			else {
				result.setResult(task.getTask());
				result.getResult().setProcsStepEdTime(DateUtil.getCurrentTimeStamp());
				result.getResult().setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				
				logger.error("Process Step Error - {}", elecDocGroupInexNo);
				
				throw new EdocException("Process Step Error - " + elecDocGroupInexNo);
			}

			// STEP 20 : PDF 생성
			if (ProcessStepCode.PDF_CREATE.getCode().compareTo(nextProcessStepCode) >= 0) {
				logger.info("========== STEP 20 : PDF Create - {} ==========", elecDocGroupInexNo);
				
				result = createPdfAction.execute(task);

				if (ProcessStatusCode.SUCCESS.getCode().equals(result.getResult().getProcsStepStcd())) {
					nextProcessStepCode = ProcessStepCode.getNextCode(nextProcessStepCode).getCode();
					task.setTask(result.getResult());
				} else {
					throw new EdocException(elecDocGroupInexNo, EdocError.PDF_CREATE_ERROR);
				}
			}
			
			// STEP 30 : TSA PDF 생성
			if (ProcessStepCode.TSA.getCode().compareTo(nextProcessStepCode) >= 0) {
				logger.info("========== STEP 30 : TSA PDF Create - {} ==========", elecDocGroupInexNo);
				
				result = createTsaAction.execute(task);
				
				if (ProcessStatusCode.SUCCESS.getCode().equals(result.getResult().getProcsStepStcd() )) {
					nextProcessStepCode = ProcessStepCode.getNextCode(nextProcessStepCode).getCode();
					task.setTask(result.getResult());
				} else {
					throw new EdocException(elecDocGroupInexNo, EdocError.TSA_CREATE_ERROR);
				}
			}
			
			// STEP 40 : 공전소 전송 (이번 프로젝트에서는 생략)
			if (ProcessStepCode.PDF_SEND.getCode().compareTo(nextProcessStepCode) >= 0) {
				nextProcessStepCode = ProcessStepCode.getNextCode(nextProcessStepCode).getCode();
				task.setTask(result.getResult());
			}
			
			// STEP 50 : PDF -> IMG 변환 (이번 프로젝트에서는 생략)
			if (ProcessStepCode.PDF_CONVERT.getCode().compareTo(nextProcessStepCode) >= 0) {
				nextProcessStepCode = ProcessStepCode.getNextCode(nextProcessStepCode).getCode();
				task.setTask(result.getResult());
			}
			
			// STEP 70 : ECM 전송
			if (ProcessStepCode.ECM.getCode().compareTo(nextProcessStepCode) >= 0) {
				logger.info("========== STEP 70 : ECM Send - {} ==========", elecDocGroupInexNo);
				
				result = sendEcmAction.execute(task);
			}
		
		} catch (EdocException e) {
			logger.error("[EdocException] Error Code : {}, Message : {}", result.getStatusCode(), result.getStatusMsg());

			return result;
		} catch (Exception e) {
			logger.error("EdocTaskWorker Error [Exception]", e);
		}

		return result;
	}
	
}
