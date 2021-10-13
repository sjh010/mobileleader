package com.mobileleader.edoc.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsErrHstrVo;
import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsMgmtVo;
import com.mobileleader.edoc.data.mapper.EdocErrorHistoryMapper;
import com.mobileleader.edoc.data.mapper.EdocProcessMapper;
import com.mobileleader.edoc.exception.EdocError;
import com.mobileleader.edoc.exception.EdocException;
import com.mobileleader.edoc.type.ProcessMessageCode;
import com.mobileleader.edoc.type.ProcessStatusCode;
import com.mobileleader.edoc.type.ProcessStepCode;
import com.mobileleader.edoc.util.DateUtil;

@Component
public class EdsTaskWorkerResultProcessorImpl implements EdsTaskWorkerResultProcessor {

	private Logger logger = LoggerFactory.getLogger(EdsTaskWorkerResultProcessorImpl.class);

	@Autowired
	private EdocProcessMapper edocProcessMapper;
	
	@Autowired
	private EdocErrorHistoryMapper edocErrorHistoryMapper;
	
	@Value("${RETRY_COUNT}")
	private int RETRY_COUNT;
	
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

	@Override
	@Transactional
	public void process(EdsTaskWorkerResult result) {

		TbEdsElecDocGroupProcsMgmtVo edocProcess = result.getResult();
		
		logger.info("Result Process Start");
		logger.info("[{}] - STEP : {}, MSG : {}, STATUS CODE : {}", edocProcess.getElecDocGroupInexNo(), edocProcess.getProcsStepCd(), edocProcess.getProcsStepMsgCd(), edocProcess.getProcsStepStcd());

		TbEdsElecDocGroupProcsErrHstrVo error = null;

		try {
			error = new TbEdsElecDocGroupProcsErrHstrVo();

			if (ProcessStepCode.ECM.getCode().equals(edocProcess.getProcsStepCd()) 
					&& ProcessStatusCode.SUCCESS.getCode().equals(edocProcess.getProcsStepStcd())) { 
				// ECM 전송 완료 및 상태 코드가 성공인 경우
				logger.info("[{}] FINISH WORK", edocProcess.getElecDocGroupInexNo());

				if (!updateProcessStatusSuccess(edocProcess)) {
					throw new EdocException("Process Status Update Error", EdocError.SQL_ERROR);
				}
			} else if (ProcessStatusCode.FAIL.getCode().equals(edocProcess.getProcsStepStcd()) ) { // 실패
				// 처리 상태 코드가 '실패' 인 경우, 실패 이력 등록.
				error.setElecDocGroupInexNo(edocProcess.getElecDocGroupInexNo());
				error.setCrtnTime(DateUtil.getCurrentTimeStamp());
				error.setProcsStepStTime(edocProcess.getProcsStepStTime());
				error.setProcsStepEdTime(edocProcess.getProcsStepEdTime());
				error.setProcsStepCd(edocProcess.getProcsStepCd());
				error.setProcsStepStcd(edocProcess.getProcsStepStcd());
				error.setProcsStepMsgCd(result.getStatusCode());
				error.setErrMsg(result.getStatusMsg());
				error.setSvrIp(edocProcess.getSvrIp());
				
				logger.info("[{}] ERROR WORK", edocProcess.getElecDocGroupInexNo());
				
				if (!updateProcessStatusError(edocProcess, error)) {
					throw new EdocException("Process Status Update Error", EdocError.SQL_ERROR);
				}
				
				if (!insertErrorHistory(error)) {
					throw new EdocException("Error History Insert Error", EdocError.SQL_ERROR);
				}
				
			}
		} catch (Exception e) {
			logger.error("EdsTaskWorkerResultProcessorImpl error ", e);
		}
	}

	/**
	 * 전자문서 성공 처리. 
	 * 
	 * @param edocProcess 전자문서 처리 VO
	 * @return
	 * @throws Exception
	 */
	@Transactional
	private boolean updateProcessStatusSuccess(TbEdsElecDocGroupProcsMgmtVo edocProcess) throws Exception {
		
		try {
			edocProcess.setProcsStepMsgCd(ProcessMessageCode.FINISH.getCode());
			
			edocProcessMapper.update(edocProcess);
		} catch (Exception e) {
			logger.error("Process Status Update Error");
			throw e;
		}

		return true;
	}

	/**
	 * 전자문서 실패 처리.
	 * 
	 * @param edocProcess 전자문서 처리 VO
	 * @param error 에러 이력 VO
	 * @return
	 * @throws Exception
	 */
	@Transactional
	private boolean updateProcessStatusError(TbEdsElecDocGroupProcsMgmtVo edocProcess, TbEdsElecDocGroupProcsErrHstrVo error) throws Exception {
		logger.debug("postProcessProcMng start");

		try {
			int errorCount = edocErrorHistoryMapper.selectDuplicateError(error);
			
			if (errorCount < RETRY_COUNT) {
				edocProcess.setProcsStepMsgCd(ProcessMessageCode.INITIAL.getCode());
			} else {
				edocProcess.setProcsStepMsgCd(ProcessMessageCode.RETRY_MAX.getCode());
			}
			
			edocProcessMapper.update(edocProcess);
		} catch (Exception e) {
			logger.error("Process Status Update Error");
			throw e;
		}
		
		return true;
	}

	/**
	 * 에러 이력 등록.
	 * 
	 * @param error 에러 VO
	 * @return
	 * @throws Exception
	 */
	@Transactional
	private boolean insertErrorHistory(TbEdsElecDocGroupProcsErrHstrVo error) throws Exception {
		
		int times = edocErrorHistoryMapper.selectDuplicateError(error);
		
		if (times == 0) {
			error.setSeqno(1);
		} else {
			error.setSeqno(times + 1);
		}
		
		try {
			int result = edocErrorHistoryMapper.insert(error);
			
			if (result != 1) {
				return false;
			}
		} catch (Exception e) {
			logger.error("Error History Insert Error");
			throw e;
		}

		return true;
	}

}
