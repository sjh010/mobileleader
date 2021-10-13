package com.mobileleader.edoc.data.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.type.Alias;

/**
 * 전자문서파일처리관리 테이블의 레코드 정보를 저장하는 Value Object
 * 
 * @author (주)인지소프트 조래훈
 * @version 1.0
 * @since 2017.09.18
 */
@Alias("edocFileProcess")
public class TbEdsElecDocFileProcsMgmtVo
{
	private String elecDocGroupInexNo;	// 전자문서그룹인덱스번호 ELEC_DOC_GROUP_INEX_NO <- EDOCGRP_IDX_NO
	private long   fileSeqNo;			// 파일순번 FILE_SEQ_NO <- FILE_SQN
	private String lefrmCd;				// 서식코드 LEFRM_CD <- FORM_CD
	private String lefrmNm;				// 서식명 LEFRM_NM <- FORM_NM
	private String xmlFileNm	;		// XML파일명 XML_FILE_NM
	private String pdfFileNm;			// PDF파일명 PDF_FILE_NM
	private int    pageCnt;				// 페이지수 PAGE_CNT
	private String procsStepCd;			// 처리단계코드 PROCS_STEP_CD <- PROC_STG_CD
	private String procsStepStcd;		// 처리단계상태코드 PROCS_STEP_STCD <- PROC_STG_STTS_CD
	private Date   crtnTime;			// 생성시각 CRTN_TIME <- CRT_TS
	private String lefrmVer;			// 서식버전 LEFRM_VER

	/**
	 * 전자문서그룹인덱스번호 ELEC_DOC_GROUP_INEX_NO
	 * 
	 * @return 전자문서그룹인덱스번호 ELEC_DOC_GROUP_INEX_NO
	 */
	public String getElecDocGroupInexNo() {
		return elecDocGroupInexNo;
	}

	public void setElecDocGroupInexNo(String elecDocGroupInexNo) {
		this.elecDocGroupInexNo = elecDocGroupInexNo;
	}

	/**
	 * 파일순번 FILE_SEQ_NO
	 * 
	 * @return 파일순번 FILE_SEQ_NO
	 */
	public long getFileSeqNo() {
		return fileSeqNo;
	}

	public void setFileSeqNo(long fileSeqNo) {
		this.fileSeqNo = fileSeqNo;
	}

	/**
	 * 서식코드 LEFRM_CD
	 * 
	 * @return 서식코드 LEFRM_CD
	 */
	public String getLefrmCd() {
		return lefrmCd;
	}

	public void setLefrmCd(String lefrmCd) {
		this.lefrmCd = lefrmCd;
	}

	/**
	 * 서식명 LEFRM_NM
	 * 
	 * @return
	 */
	public String getLefrmNm() {
		return lefrmNm;
	}

	public void setLefrmNm(String lefrmNm) {
		this.lefrmNm = lefrmNm;
	}

	/**
	 * XML파일명 XML_FILE_NM
	 * 
	 * @return XML파일명 XML_FILE_NM
	 */
	public String getXmlFileNm() {
		return xmlFileNm;
	}

	public void setXmlFileNm(String xmlFileNm) {
		this.xmlFileNm = xmlFileNm;
	}

	/**
	 * PDF파일명 PDF_FILE_NM
	 * 
	 * @return PDF파일명 PDF_FILE_NM
	 */
	public String getPdfFileNm() {
		return pdfFileNm;
	}

	public void setPdfFileNm(String pdfFileNm) {
		this.pdfFileNm = pdfFileNm;
	}

	/**
	 * 페이지수 PAGE_CNT
	 * 
	 * @return 페이지수 PAGE_CNT
	 */
	public int getPageCnt() {
		return pageCnt;
	}

	public void setPageCnt(int pageCnt) {
		this.pageCnt = pageCnt;
	}

	/**
	 * 처리단계코드 PROCS_STEP_CD
	 * 
	 * @return 처리단계코드 PROCS_STEP_CD
	 */
	public String getProcsStepCd() {
		return procsStepCd;
	}

	public void setProcsStepCd(String procsStepCd) {
		this.procsStepCd = procsStepCd;
	}

	/**
	 * 처리단계상태코드 PROCS_STEP_STCD
	 * 
	 * @return 처리단계상태코드 PROCS_STEP_STCD
	 */
	public String getProcsStepStcd() {
		return procsStepStcd;
	}

	public void setProcsStepStcd(String procsStepStcd) {
		this.procsStepStcd = procsStepStcd;
	}

	/**
	 * 전자문서파일 생성 시각 CRTN_TIME
	 * 
	 * @return 전자문서파일 생성 시각
	 */
	public Date getCrtnTime() {
		return crtnTime;
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

	public void setCrtnTime(Date crtnTime) {
		this.crtnTime = crtnTime;
	}

	/**
	 * 전자문서파일(PDF) 생성 시각을 설정한다.
	 * 
	 * @param crtnTime
	 *            전자문서파일(PDF) 생성 시각 문자열.<br>
	 * 
	 *            <pre>
	 * 포맷 "yyyyMMddHHmmss".<br>
	 * (예제) setCrtTsString("2017/09/17 11:39:59");
	 * </pre>
	 * @throws ParseException
	 * @throws java.text.ParseException
	 * @throws java.text.ParseException
	 */
	public void setCrtnTimeString(String crtnTime) throws ParseException, java.text.ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		this.crtnTime = timeFormat.parse(crtnTime);
	}

	/** 서식버전 LEFRM_VER */
	public String getLefrmVer() {
		return lefrmVer;
	}
	/** 서식버전 LEFRM_VER */
	public void setLefrmVer(String lefrmVer) {
		this.lefrmVer = lefrmVer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("TbEDS001M30Vo [\n");
		sb.append("\t").append("elecDocGroupInexNo = ").append(elecDocGroupInexNo).append(", ").append("\n");
		sb.append("\t").append("fileSeqNo          = ").append(fileSeqNo         ).append(", ").append("\n");
		sb.append("\t").append("lefrmCd            = ").append(lefrmCd           ).append(", ").append("\n");
		sb.append("\t").append("lefrmNm            = ").append(lefrmNm           ).append(", ").append("\n");
		sb.append("\t").append("xmlFileNm          = ").append(xmlFileNm         ).append(", ").append("\n");
		sb.append("\t").append("pdfFileNm          = ").append(pdfFileNm         ).append(", ").append("\n");
		sb.append("\t").append("pageCnt            = ").append(pageCnt           ).append(", ").append("\n");
		sb.append("\t").append("procsStepCd        = ").append(procsStepCd       ).append(", ").append("\n");
		sb.append("\t").append("procsStepStcd      = ").append(procsStepStcd     ).append(", ").append("\n");
		sb.append("\t").append("crtnTime           = ").append(crtnTime          ).append(", ").append("\n");
		sb.append("\t").append("lefrmVer           = ").append(lefrmVer          ).append(", ").append("\n");
		sb.append("]");

		return sb.toString();
	}
}
