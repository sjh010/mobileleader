package com.mobileleader.edoc.data.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * 전자문서서버에서 전자문서 처리 시 발생한 오류 이력
 * 
 * @author (주)모바일리더 이철원
 * @version 1.0
 * @since 2017.10.10
 */
@Alias("edocErrorInfo")
public class TbEdsElecDocGroupProcsErrHstrVo {
	private String elecDocGroupInexNo;	// 전자문서그룹인덱스번호 ELEC_DOC_GROUP_INEX_NO
	private int seqno;					// 일련번호 SEQNO
	private Date   crtnTime;			// 생성시각 CRTN_TIME
	private String procsStepCd;			// 처리단계코드 PROCS_STEP_CD
	private String procsStepStcd;		// 처리단계상태코드 PROCS_STEP_STCD
	private String procsStepMsgCd;		// 처리단계 메시지 코드 PROCS_STEP_MSG_CD
	private Date   procsStepStTime;		// 처리단계시작시각 PROCS_STEP_ST_TIME
	private Date   procsStepEdTime;		// 처리단계종료시각 PROCS_STEP_ED_TIME
	private String errMsg;				// 오류메시지	ERR_MSG
	private String svrIp;				// server IP
	/** 전자문서그룹인덱스번호 ELEC_DOC_GROUP_INEX_NO */
	public String getElecDocGroupInexNo() {
		return elecDocGroupInexNo;
	}
	/** 전자문서그룹인덱스번호 ELEC_DOC_GROUP_INEX_NO */
	public void setElecDocGroupInexNo(String elecDocGroupInexNo) {
		this.elecDocGroupInexNo = elecDocGroupInexNo;
	}
	public int getSeqno() {
		return seqno;
	}
	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}
	/** 전자문서파일 생성시각 CRTN_TIME */
	public Date getCrtnTime() {
		return crtnTime;
	}
	/** 전자문서파일 생성시각 CRTN_TIME */
	public void setCrtnTime(Date crtnTime) {
		this.crtnTime = crtnTime;
	}
	/**
	 * 전자문서파일 생성 시각을 획득한다.
	 * 
	 * @return String 문자열. "yyyyMMddHHmmss" 포맷.
	 */
	public String getCrtnTimeString() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.lang.String timeString = timeFormat.format(crtnTime);
		return timeString;
	}
	/**
	 * 전자문서파일(PDF) 생성 시각을 획득한다. <br>
	 * 포맷 참고
	 * 
	 * <pre>
	 * 년월일시분초 : "yyyyMMddHHmmss"<br>
	 * 년월일 : "yyyyMMdd"<br>
	 * 시분초 : "HHmmss"
	 * </pre>
	 * 
	 * @param format
	 *            획득하고자 하는 전자문서파일 생성 시각의 포맷
	 * @return String 문자열
	 */
	public String getCrtnTimeString(String format) {
		if (crtnTime == null)
			return null;

		SimpleDateFormat timeFormat = new SimpleDateFormat(format);
		String timeString = timeFormat.format(crtnTime);
		return timeString;
	}
	/**
	 * 전자문서파일(PDF) 생성 시각을 설정한다.
	 * 
	 * @param crtnTimeString
	 *            전자문서파일(PDF) 생성 시각 문자열.<br>
	 * 
	 *            <pre>
	 * 포맷 "yyyyMMddHHmmss".<br>
	 * (예제) setCrtTsString("20170917113959");
	 * </pre>
	 * @throws ParseException
	 * @throws java.text.ParseException
	 * @throws java.text.ParseException
	 */
	public void setCrtnTimeString(String crtnTimeString) throws ParseException, java.text.ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		this.crtnTime = timeFormat.parse(crtnTimeString);
	}

	/** 처리단계코드 PROCS_STEP_CD */
	public String getProcsStepCd() {
		return procsStepCd;
	}
	/** 처리단계코드 PROCS_STEP_CD */
	public void setProcsStepCd(String procsStepCd) {
		this.procsStepCd = procsStepCd;
	}

	/** 처리단계상태코드 PROCS_STEP_STCD */
	public String getProcsStepStcd() {
		return procsStepStcd;
	}
	/** 처리단계상태코드 PROCS_STEP_STCD */
	public void setProcsStepStcd(String procsStepStcd) {
		this.procsStepStcd = procsStepStcd;
	}

	/** 처리단계메시지코드 PROCS_STEP_MSG_CD */
	public String getProcsStepMsgCd() {
		return procsStepMsgCd;
	}
	/** 처리단계메시지코드 PROCS_STEP_MSG_CD */
	public void setProcsStepMsgCd(String procsStepMsgCd) {
		this.procsStepMsgCd = procsStepMsgCd;
	}

	/** 처리단계시작시각 PROCS_STEP_ST_TIME */
	public Date getProcsStepStTime() {
		return procsStepStTime;
	}
	/**
	 * 처리단계시작시각 PROCS_STEP_ST_TIME
	 * 
	 * @return String 문자열. "yyyyMMddHHmmss" 포맷.
	 */
	public String getProcsStepStTimeString() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.lang.String timeString = timeFormat.format(procsStepStTime);
		return timeString;
	}
	/**
	 * 처리단계시작시각 PROCS_STEP_ST_TIME <br>
	 * 포맷 참고
	 * 
	 * <pre>
	 * 년월일시분초 : "yyyyMMddHHmmss"<br>
	 * 년월일 : "yyyyMMdd"<br>
	 * 시분초 : "HHmmss"
	 * </pre>
	 * 
	 * @param format
	 *            획득하고자 하는 처리단계 시작 시각의 포맷
	 * @return String 문자열
	 */
	public String getProcsStepStTimeString(String format) {
		if (procsStepStTime == null)
			return null;

		SimpleDateFormat timeFormat = new SimpleDateFormat(format);
		String timeString = timeFormat.format(procsStepStTime);
		return timeString;
	}
	/** 처리단계시작시각 PROCS_STEP_ST_TIME */
	public void setProcsStepStTime(Date procsStepStTime) {
		this.procsStepStTime = procsStepStTime;
	}
	/**
	 * 처리단계시작시각 PROCS_STEP_ST_TIME
	 * 
	 * @param crtnTime
	 *            처리단계시작 시각 문자열.<br>
	 * 
	 *            <pre>
	 * 포맷 "yyyyMMddHHmmss".<br>
	 * (예제) setProcStgStTsString("20170917113959");
	 * </pre>
	 * @throws ParseException
	 * @throws java.text.ParseException
	 * @throws java.text.ParseException
	 */
	public void setProcsStepStTimeString(String procsStepStTime) throws ParseException, java.text.ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		this.procsStepStTime = timeFormat.parse(procsStepStTime);
	}

	/** 처리단계종료시각 PROCS_STEP_ED_TIME */
	public Date getProcsStepEdTime() {
		return procsStepEdTime;
	}
	/**
	 * 처리단계종료시각 PROCS_STEP_ED_TIME
	 * 
	 * @return String 문자열. "yyyyMMddHHmmss" 포맷.
	 */
	public String getProcsStepEdTimeString() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.lang.String timeString = timeFormat.format(procsStepEdTime);
		return timeString;
	}
	/**
	 * 처리단계종료시각 PROCS_STEP_ED_TIME <br>
	 * 포맷 참고
	 * 
	 * <pre>
	 * 년월일시분초 : "yyyyMMddHHmmss"<br>
	 * 년월일 : "yyyyMMdd"<br>
	 * 시분초 : "HHmmss"
	 * </pre>
	 * 
	 * @param format
	 *            획득하고자 하는 처리단계종료 시각의 포맷
	 * @return String 문자열
	 */
	public String getProcsStepEdTimeString(String format) {
		if (procsStepEdTime == null)
			return null;

		SimpleDateFormat timeFormat = new SimpleDateFormat(format);
		String timeString = timeFormat.format(procsStepEdTime);
		return timeString;
	}
	/** 처리단계종료시각 PROCS_STEP_ED_TIME */
	public void setProcsStepEdTime(Date procsStepEdTime) {
		this.procsStepEdTime = procsStepEdTime;
	}
	/**
	 * 처리단계종료시각 PROCS_STEP_ED_TIME
	 * 
	 * @param procStgEdTs
	 *            전자문서파일(PDF) 처리단계종료 시각 문자열.<br>
	 * 
	 *            <pre>
	 * 포맷 "yyyyMMddHHmmss".<br>
	 * (예제) setProcStgEdTsString("20170917113959");
	 * </pre>
	 * @throws ParseException
	 * @throws java.text.ParseException
	 * @throws java.text.ParseException
	 */
	public void setProcsStepEdTimeString(String procsStepEdTime) throws ParseException, java.text.ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		this.procsStepEdTime = timeFormat.parse(procsStepEdTime);
	}

	/** 오류메시지	ERR_MSG */
	public String getErrMsg() {
		return errMsg;
	}
	/** 오류메시지	ERR_MSG */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	public String getSvrIp() {
		return svrIp;
	}
	public void setSvrIp(String svrIp) {
		this.svrIp = svrIp;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("TbEdsElecDocGroupProcsErrHstrVo [");
		sb.append("\t").append("elecDocGroupInexNo = ").append(elecDocGroupInexNo).append(", ").append("\n");
		sb.append("\t").append("seqno              = ").append(seqno             ).append(", ").append("\n");
		sb.append("\t").append("crtnTime           = ").append(crtnTime          ).append(", ").append("\n");
		sb.append("\t").append("procsStepCd        = ").append(procsStepCd       ).append(", ").append("\n");
		sb.append("\t").append("procsStepStcd      = ").append(procsStepStcd     ).append(", ").append("\n");
		sb.append("\t").append("procsStepMsgCd     = ").append(procsStepMsgCd    ).append(", ").append("\n");
		sb.append("\t").append("procsStepStTime    = ").append(procsStepStTime   ).append(", ").append("\n");
		sb.append("\t").append("procsStepEdTime    = ").append(procsStepEdTime   ).append(", ").append("\n");
		sb.append("\t").append("errMsg             = ").append(errMsg            ).append(", ").append("\n");
		sb.append("\t").append("svrIp              = ").append(svrIp             )             .append("\n");
		sb.append("]");

		return sb.toString();
	}

}
