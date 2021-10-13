package com.mobileleader.edoc.data.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * 전자문서그룹업무정보 테이블의 레코드 정보를 저장하는 Value Object
 * 
 * @author 인지소프트
 * @version 1.0
 * @since 2017.09.18
 */
@Alias("edocBiz")
public class TbEdsElecDocGroupBzwkInfoVo
{
	private String elecDocGroupInexNo;		// 전자문서그룹인덱스번호	ELEC_DOC_GROUP_INEX_NO <- EDOCGRP_IDX_NO
	private String scnNo;					// 화면번호			SCN_NO <- SCRN_NO
	private String subScrnNo;				// 부화면번호
	private String dsrbCd;					// 취급점코드			DSRB_CD <- DLBR_CD
	private String dsrbNm;					// 취급점명			DSRB_NM <- DLBR_NM
	private String hndrNo;					// 취급자직번			HNDR_NO <- DLPE_NO
	private String hndrNm;					// 취급자명			HNDR_NM <- DLPE_NM
	private Date   regTime;					// 등록시각			REG_TIME <- RGST_TS
	private String prcsTycd;				// 프로세스유형코드		PRCS_TYCD
	private String trmnNo;					// 단말번호			TRMN_NO

	// 신복위 추가
	private String mainKey;					// 통합전자문서키(메인키) MAIN_KEY
	private String taskKey;					// 업무키(접수번호)	    TASK_KEY
	private String customerName;			// 고객명				CUSTOMER_NAME
	private String insourceId;				// 업무코드			INSOURCE_ID
	private String insourceTitle;			// 업무명				INSOURCE_TITLE
	private String memo;					// 메모				MEMO

	public String getElecDocGroupInexNo() {
		return elecDocGroupInexNo;
	}
	public void setElecDocGroupInexNo(String elecDocGroupInexNo) {
		this.elecDocGroupInexNo = elecDocGroupInexNo;
	}
	public String getScnNo() {
		return scnNo;
	}
	public void setScnNo(String scnNo) {
		this.scnNo = scnNo;
	}
	public String getSubScrnNo() {
		return subScrnNo;
	}
	public void setSubScrnNo(String subScrnNo) {
		this.subScrnNo = subScrnNo;
	}
	public String getDsrbCd() {
		return dsrbCd;
	}
	public void setDsrbCd(String dsrbCd) {
		this.dsrbCd = dsrbCd;
	}
	public String getDsrbNm() {
		return dsrbNm;
	}
	public void setDsrbNm(String dsrbNm) {
		this.dsrbNm = dsrbNm;
	}
	public String getHndrNo() {
		return hndrNo;
	}
	public void setHndrNo(String hndrNo) {
		this.hndrNo = hndrNo;
	}
	public String getHndrNm() {
		return hndrNm;
	}
	public void setHndrNm(String hndrNm) {
		this.hndrNm = hndrNm;
	}
	public Date getRegTime() {
		return regTime;
	}
	/**
	 * 전자문서그룹업무정보 등록시각을 획득한다.
	 * @return	등록시각 String 문자열. "yyyyMMddHHmmss" 포맷.
	 */
	public String getRegTimeString() {
		if(regTime == null) return null;

		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.lang.String timeString = timeFormat.format(regTime);
		return timeString;
	}
	/**
	 * 전자문서그룹업무정보 등록시각을 획득한다.
	 * <br>
	 * 포맷 참고
	 * <pre>
	 * 년월일시분초 : "yyyyMMddHHmmss"<br>
	 * 년월일 : "yyyyMMdd"<br>
	 * 시분초 : "HHmmss"
	 * </pre>
	 * @param	format	획득하고자 하는 전자문서그룹업무정보 등록시각의 포맷
	 * @return	등록시각 String 문자열
	 */
	public String getRegTimeString(String format) {
		if(regTime == null) return null;

		SimpleDateFormat timeFormat = new SimpleDateFormat(format);
		String timeString = timeFormat.format(regTime);
		return timeString;
	}
	/**
	 * 전자문서그룹 생성 시각을 설정한다.
	 * @param	rgstTs	java.util.Date 전자문서그룹 생성 시각
	 */
	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}
	/**
	 * 전자문서그룹 생성 시각을 설정한다.
	 * @param	rgstTs	String 전자문서그룹 생성 시각<br>
	 * <pre>
	 * 포맷 "yyyyMMddHHmmss".<br>
	 * (예제) setRgstTsString("20170917113959");
	 * </pre>
	 * @throws	ParseException
	 * @throws java.text.ParseException 
	 * @throws java.text.ParseException 
	 */
	public void setRegTimeString(String regTime) throws ParseException, java.text.ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		this.regTime = timeFormat.parse(regTime);
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getPrcsTycd() {
		return prcsTycd;
	}
	public void setPrcsTycd(String prcsTycd) {
		this.prcsTycd = prcsTycd;
	}
	public String getTrmnNo() {
		return trmnNo;
	}
	public void setTrmnNo(String trmnNo) {
		this.trmnNo = trmnNo;
	}
	public String getMainKey() {
		return mainKey;
	}
	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}
	public String getTaskKey() {
		return taskKey;
	}
	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getInsourceId() {
		return insourceId;
	}
	public void setInsourceId(String insourceId) {
		this.insourceId = insourceId;
	}
	public String getInsourceTitle() {
		return insourceTitle;
	}
	public void setInsourceTitle(String insourceTitle) {
		this.insourceTitle = insourceTitle;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TbEdsElecDocGroupBzwkInfoVo [");
		if (elecDocGroupInexNo != null)
			builder.append("elecDocGroupInexNo=").append(elecDocGroupInexNo).append(", ");
		if (scnNo != null)
			builder.append("scnNo=").append(scnNo).append(", ");
		if (subScrnNo != null)
			builder.append("subScrnNo=").append(subScrnNo).append(", ");
		if (dsrbCd != null)
			builder.append("dsrbCd=").append(dsrbCd).append(", ");
		if (dsrbNm != null)
			builder.append("dsrbNm=").append(dsrbNm).append(", ");
		if (hndrNo != null)
			builder.append("hndrNo=").append(hndrNo).append(", ");
		if (hndrNm != null)
			builder.append("hndrNm=").append(hndrNm).append(", ");
		if (regTime != null)
			builder.append("regTime=").append(regTime).append(", ");
		if (prcsTycd != null)
			builder.append("prcsTycd=").append(prcsTycd).append(", ");
		if (trmnNo != null)
			builder.append("trmnNo=").append(trmnNo).append(", ");
		if (mainKey != null)
			builder.append("mainKey=").append(mainKey).append(", ");
		if (taskKey != null)
			builder.append("taskKey=").append(taskKey).append(", ");
		if (customerName != null)
			builder.append("customerName=").append(customerName).append(", ");
		if (insourceId != null)
			builder.append("insourceId=").append(insourceId).append(", ");
		if (insourceTitle != null)
			builder.append("insourceTitle=").append(insourceTitle).append(", ");
		if (memo != null)
			builder.append("memo=").append(memo);
		builder.append("]");
		return builder.toString();
	}

}
