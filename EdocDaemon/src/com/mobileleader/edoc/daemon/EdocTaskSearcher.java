package com.mobileleader.edoc.daemon;

import java.util.ArrayList;
import java.util.List;

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

/**
 * 전자문서 처리 업무 검색 클래스
 */
@Component
@Transactional
public class EdocTaskSearcher {
	
	private Logger logger = LoggerFactory.getLogger(EdocTaskSearcher.class);

	@Autowired
	private EdocProcessMapper edocProcessMapper;
	
	@Autowired
	private EdocErrorHistoryMapper edocErrorHistoryMapper;
	
	/**
	 * (클라이언트와 통신하는) 전자문서생성 서버와 (전자문서를 생성하는) 전자문서 생성 데몬이 다른 서버에 있는 경우,
	 * 
	 * 전자문서생성 서버의 IP를 알아야 Task 검색이 가능함.
	 * 
	 * 같은 서버에 설치되어 있는 경우는 해당 IP를 사용하면된다.
	 */
	@Value("${REGIST_SERVER_IP:}")
	private String registServerIp;
	
	/**
	 * 처리 할 업무 검색
	 */
	@Transactional
	public List<EdsTask> search() {
		
		List<EdsTask> edocTaskList = null;
		List<TbEdsElecDocGroupProcsMgmtVo> edocProcessList = null;

		try {
			edocTaskList = new ArrayList<EdsTask>();
			
			// 전자문서 생성 서버와 전자문서 생성 데몬이 같은 서버에 있는 경우는 해당 IP를 사용한다.
			//String serverIp = InetAddress.getLocalHost().getHostAddress();
			
			// 해당 고객사는 전자문서 생성 서버와 생성 데몬이 다른 서버에 있으므로 전자문서 생성 서버 IP 사용
			edocProcessList = edocProcessMapper.selectTask(registServerIp);
			
			if (edocProcessList == null || edocProcessList.size() == 0) {
				logger.debug("Search Result : No Task Searched");
			} else {
				for (TbEdsElecDocGroupProcsMgmtVo edocProcess : edocProcessList) {	
					try {
						// 상태 초기화
						if (initializeTask(edocProcess)) {
							EdsTask task = new EdsTask(edocProcess);
							edocTaskList.add(task);
						}
					} catch (EdocException e) {
						updateErrorTask(edocProcess);
					}
					
				}
				
				logger.info("Search Result : {} Task Searched", edocTaskList.size());
			}
		} catch (Exception e) {
			logger.error("Task Search() Exception", e);
		}

		return edocTaskList;
	}
	
	/**
	 * 처리 시작 시간 및 상태(진행중) 변경
	 * 
	 * @param edocProcess
	 * @return
	 * @throws EdocException
	 */
	@Transactional
	private boolean initializeTask(TbEdsElecDocGroupProcsMgmtVo edocProcess) throws EdocException {

		boolean result = false;
		
		try {
			edocProcess.setProcsStepMsgCd(ProcessMessageCode.CONVERT_START.getCode());
			edocProcess.setProcsStepStTimeString(DateUtil.getDateTimeString());
			edocProcessMapper.update(edocProcess);

			result = true;
		} catch (Exception e) {
			logger.error("initializeTask Error : {}", e.getMessage());
			throw new EdocException("initializeTask Error", EdocError.SQL_ERROR);
		}
		
		return result;
	}
	
	/**
	 * 초기화 에러 상태 처리
	 * 
	 * @param edocProcess
	 */
	@Transactional
	private void updateErrorTask(TbEdsElecDocGroupProcsMgmtVo edocProcess) {

		logger.info("Error Task : [{}]", edocProcess.getElecDocGroupInexNo());
		
		try {
			// '입력데이터검증' 단계에서는 실패 상태로 변경하면 다음번 작업 검색 때 찾을 수 없기 때문에 상태변경 하지 않음
			if (!ProcessStepCode.INPUT_DATA_VALIDATE.getCode().equals(edocProcess.getProcsStepCd())) {
				edocProcess.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
				edocProcess.setProcsStepMsgCd(ProcessMessageCode.INITIAL.getCode());
				edocProcess.setProcsStepEdTimeString(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
				edocProcessMapper.update(edocProcess);
			}
			
			// 전자문서 처리상태 오류이력 테이블에 전자문서처리 오류이력 정보 추가
			TbEdsElecDocGroupProcsErrHstrVo edocErrorHistory = new TbEdsElecDocGroupProcsErrHstrVo();
			edocErrorHistory.setElecDocGroupInexNo(edocProcess.getElecDocGroupInexNo());
			edocErrorHistory.setProcsStepCd(edocProcess.getProcsStepCd());
			edocErrorHistory.setProcsStepStcd(ProcessStatusCode.FAIL.getCode());
			edocErrorHistory.setProcsStepMsgCd(EdocError.SQL_ERROR.getCode());
			edocErrorHistory.setProcsStepStTime(edocProcess.getProcsStepStTime());
			edocErrorHistory.setProcsStepEdTimeString(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
			edocErrorHistory.setCrtnTimeString(DateUtil.getCurrentDate("yyyyMMddHHmmss"));
			edocErrorHistory.setSvrIp(registServerIp);
			edocErrorHistory.setErrMsg("initializeTask Error");

			edocErrorHistoryMapper.insert(edocErrorHistory);

		} catch (Exception e) {
			logger.error("errorTask() error : {}", e.getMessage());
		}

	}

}
