package com.mobileleader.edoc.daemon;

import com.mobileleader.edoc.data.dao.TbEdsElecDocGroupProcsMgmtVo;

/**
 * 전자문서생성 데몬에서 처리할 업무 class
 */
public class EdsTask {
	
	private TbEdsElecDocGroupProcsMgmtVo task;
	
	private Integer statusCode;
	
	private String statusMsg;

	public EdsTask(TbEdsElecDocGroupProcsMgmtVo task) {
		this.task = task;
	}

	public EdsTask(TbEdsElecDocGroupProcsMgmtVo task, Integer statusCode, String statusMsg) {
		super();
		this.task = task;
		this.statusCode = statusCode;
		this.statusMsg = statusMsg;
	}

	public TbEdsElecDocGroupProcsMgmtVo getTask() {
		return task;
	}

	public void setTask(TbEdsElecDocGroupProcsMgmtVo task) {
		this.task = task;
	}

	public Integer getTaskCode() {
		return statusCode;
	}

	public void setTaskCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getTaskMsg() {
		return statusMsg;
	}

	public void setTaskMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EdsTask [");
		if (task != null) {
			builder.append("task=");
			builder.append(task);
			builder.append(", ");
		}
		if (statusCode != null) {
			builder.append("statusCode=");
			builder.append(statusCode);
			builder.append(", ");
		}
		if (statusMsg != null) {
			builder.append("statusMsg=");
			builder.append(statusMsg);
		}
		builder.append("]");
		return builder.toString();
	}
	
}
