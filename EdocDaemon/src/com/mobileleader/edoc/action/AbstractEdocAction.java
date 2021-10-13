package com.mobileleader.edoc.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.mobileleader.edoc.daemon.EdsTask;
import com.mobileleader.edoc.daemon.EdsTaskWorkerResult;
import com.mobileleader.edoc.data.dao.TbEdsElecDocFileProcsMgmtVo;
import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupBzwkInfoVo;
import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsMgmtVo;
import com.mobileleader.edoc.data.mapper.EdocBizMapper;
import com.mobileleader.edoc.data.mapper.EdocFileProcessMapper;
import com.mobileleader.edoc.data.mapper.EdocProcessMapper;
import com.mobileleader.edoc.exception.EdocError;
import com.mobileleader.edoc.exception.EdocException;

@Component
public abstract class AbstractEdocAction implements EdocAction {

	@Autowired
	private EdocProcessMapper edocProcessMapper;

	@Autowired
	private EdocBizMapper edocBizMapper;

	@Autowired
	private EdocFileProcessMapper edocFileProcessMapper;

	private Logger logger = LoggerFactory.getLogger(AbstractEdocAction.class);

	public abstract EdsTaskWorkerResult execute(EdsTask task) throws Exception;
	
	/**
	 * 전자문서 처리 대상 파일처리 목록 조회.
	 * 
	 * @param edocIndexNo 전자문서그룹인덱스번호
	 * @return 파일 목록 VO List
	 * @throws Exception
	 */
	@Transactional
	public List<TbEdsElecDocFileProcsMgmtVo> getFileProcessList(String edocIndexNo) throws Exception {

		List<TbEdsElecDocFileProcsMgmtVo> fileProcessList = new ArrayList<TbEdsElecDocFileProcsMgmtVo>();

		try {
			fileProcessList.addAll(edocFileProcessMapper.selectListByEdocIndexNo(edocIndexNo));

			if (fileProcessList.size() == 0) {
				logger.error("File Process List Not Found. [elecDocGroupIndexNo : {}]", edocIndexNo);
				throw new EdocException("File Process List Not Found. [elecDocGroupIndexNo : " + edocIndexNo + "]", EdocError.SQL_ERROR);
			}
		} catch (Exception e) {
			logger.error("Get File Process List Fail [elecDocGroupIndexNo : {}]", edocIndexNo, e);
			throw e;
		}

		return fileProcessList;
	}

	/**
	 * 전자문서 업무정보 조회.
	 * 
	 * @param edocIndexNo 전자문서그룹인덱스번호
	 * @return 전자문서 업무 VO
	 * @throws Exception
	 */
	@Transactional
	public TbEdsElecDocGroupBzwkInfoVo getProcessBizInfo(String edocIndexNo) throws Exception {

		TbEdsElecDocGroupBzwkInfoVo tbEdocGrpBizInfo = null;

		try {
			tbEdocGrpBizInfo = edocBizMapper.select(edocIndexNo);

			if (ObjectUtils.isEmpty(tbEdocGrpBizInfo)) {
				logger.error("Edoc Biz Info Not Found [elecDocGroupIndexNo : {}]", edocIndexNo);
				throw new EdocException("Could Not Find Process Biz", EdocError.SQL_ERROR);
			}
		} catch (Exception e) {
			logger.error("Get Edoc Biz info fail [elecDocGroupIndexNo : {}]", edocIndexNo, e);
			throw e;
		}

		return tbEdocGrpBizInfo;
	}

	/**
	 * 전자문서 처리상태 변경
	 * @param edocProcess 전자문서 처리 VO
	 */
	@Transactional
	public void updateProcessStatus(TbEdsElecDocGroupProcsMgmtVo edocProcess) throws Exception {
		
		String edocIndexNo = edocProcess.getElecDocGroupInexNo();

		try {
			int result = edocProcessMapper.update(edocProcess);

			if (result != 1) {
				logger.error("Could Not Find Process", edocIndexNo);
				throw new EdocException("Could Not Find Process", EdocError.SQL_ERROR);
			}
		} catch (Exception e) {
			logger.error("Process update fail [{}]", edocIndexNo, e);

			throw e;
		}

	}
	
	/**
	 * 전자문서 파일 처리 상태 수정.
	 * 
	 * @param file 전자문서 파일 처리 VO
	 * @throws Exception
	 */
	@Transactional
	public void updateFileProcessStatus(TbEdsElecDocFileProcsMgmtVo file) throws Exception {

		String edocIndexNo = file.getElecDocGroupInexNo();
		long fileSeqNo = file.getFileSeqNo();

		try {
			int result = edocFileProcessMapper.update(file);

			if (result != 1) {
				logger.error("Could Not Find [{} - Seq : {}]", edocIndexNo, fileSeqNo);
				throw new EdocException("Could Not Find File [ElecDocGroupInexNo : " + edocIndexNo + ", fileSeqNo : "
						+ fileSeqNo + "]", EdocError.SQL_ERROR);
			}
		} catch (Exception e) {
			logger.error("File Process Update fail [{} - {}]", edocIndexNo, fileSeqNo, e);
			throw e;
		}
	}

}
