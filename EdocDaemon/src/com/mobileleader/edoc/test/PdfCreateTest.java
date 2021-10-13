package com.mobileleader.edoc.test;

import com.inzisoft.server.pdf.PDFEditor;
import com.inzisoft.server.pdf.PDFEditorJNI;

/**
 * 전자문서 PDF 파일 생성 테스트
 * 
 * FORM_DIR_PATH : 기본 전자서식 form이 정의되어 있는 디렉토리(pdf, xml, izsv)
 * XML_DIR_PATH  : 사용자가 입력한 결과 xml 파일 및 이미지가 저장되어 있는 디렉토리
 *
 */
public class PdfCreateTest {
	
	// 로컬
	private static final String INZI_PATH = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon";
	private static final String FORM_DIR_PATH = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\test\\1.createPdf\\form";
	private static final String XML_DIR_PATH = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\test\\1.createPdf\\xml";
	
	// 개발
	//private static final String INZI_PATH = "/programs/app/edocDaemon";
	//private static final String FORM_DIR_PATH = "/programs/app/edocDaemon/test/1.createPdf/form";
	//private static final String XML_DIR_PATH = "/programs/app/edocDaemon/test/1.createPdf/xml";
	
	public void makePdfFile(String makexmlPath, String resultPDFPath) {

		System.out.println("Make Pdf Start");
		
		int retMakePDF = -999;
		
		PDFEditor pdfEditor = new PDFEditor(INZI_PATH, XML_DIR_PATH);

		try {
			retMakePDF = pdfEditor.Make(FORM_DIR_PATH, makexmlPath, resultPDFPath);
			System.out.println("Result Code : " + retMakePDF);
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		
		if (retMakePDF == 0) {
			System.out.println("PDF Make Success");
		} else {
			System.out.println("PDF Make Fail");
		}
	}

	// PDF Page Count
	public int getPdfPageCount(String resultPDFPath) {
		int openDocIdx = PDFEditorJNI.openDocument(resultPDFPath);
		int pageCount = PDFEditorJNI.countPages(openDocIdx);

		PDFEditorJNI.closeDocument(openDocIdx);

		return pageCount;
	}
	
	public static void main(String[] args) {
		
		PdfCreateTest test = new PdfCreateTest();

		// 로컬
		//String xmlFilePath =  "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\test\\1.createPdf\\xml\\002006_result.xml";
		//String pdfDir = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\test\\1.createPdf\\pdf\\result.pdf";
		
		// 개발
		String xmlFilePath =  "/programs/app/edocDaemon/test/1.createPdf/xml/002006_result.xml";
		String pdfDir = "/programs/app/edocDaemon/test/1.createPdf/pdf/result.pdf";
		
		test.makePdfFile(xmlFilePath, pdfDir);
	}
	
}
