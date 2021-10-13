package com.mobileleader.edoc.daemon;

import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupBzwkInfoVo;
import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsMgmtVo;

/**
 * 전자문서생성 데몬에서 개별 업무 수행 결과를 저장하는 클래스
 */
public class EdsTaskWorkerResult {
	private TbEdsElecDocGroupProcsMgmtVo result;
	private TbEdsElecDocGroupBzwkInfoVo bzwkInfo;
	private String statusCode;
	private String statusMsg;

	public EdsTaskWorkerResult() {
		super();
		result = new TbEdsElecDocGroupProcsMgmtVo();
	}

	public EdsTaskWorkerResult(TbEdsElecDocGroupProcsMgmtVo result, TbEdsElecDocGroupBzwkInfoVo bzwkInfo) {
		super();
		this.result = result;
		this.bzwkInfo = bzwkInfo;
		this.statusCode = "";
		this.statusMsg = "";
	}

	public TbEdsElecDocGroupBzwkInfoVo getBzwkInfo() {
		return bzwkInfo;
	}

	public void setBzwkInfo(TbEdsElecDocGroupBzwkInfoVo bzwkInfo) {
		this.bzwkInfo = bzwkInfo;
	}

	public TbEdsElecDocGroupProcsMgmtVo getResult() {
		return result;
	}

	public void setResult(TbEdsElecDocGroupProcsMgmtVo result) {
		this.result = result;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		// 에러 메세지 1000byte 짜르기
		byte[] errMsgArr = statusMsg.getBytes();
		byte[] msgSetArr = new byte[1000];
		if(errMsgArr.length > 1000){
			System.arraycopy(errMsgArr, 0, msgSetArr, 0, msgSetArr.length);
			statusMsg = new String(msgSetArr);
		}
		this.statusMsg = statusMsg;
	}

	@Override
	public String toString() {
		return "EdsTaskWorkerResult [result=" + result.toString() + ", statusCode=" + statusCode + ", statusMsg="
				+ statusMsg + "]";
	}

}
