package com.mobileleader.edoc.test;

import java.io.File;

import com.mobileleader.tsa.common.PropertiesConfig;
import com.mobileleader.tsa.common.TSACommonException;
import com.mobileleader.tsa.pdfsign.PdfSigner;
import com.mobileleader.tsa.pdfsign.PdfSignerException;
import com.mobileleader.tsa.pdfsign.PdfSignerImpl;
import com.mobileleader.tsa.pdfsign.PdfSignerParameter;

public class TsaTest {

	public static void main(String[] args) {
		
		String TSA_CONF_PATH = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\tsa\\pdfsigner.properties";

		String pdfPath = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\test\\2.tsa\\pdf\\sample.pdf";
		String outPdfPath = "C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\test\\2.tsa\\tsapdf\\after.pdf";
		
		PropertiesConfig config = new PropertiesConfig();

		try {
			config.load(TSA_CONF_PATH);
			System.out.println("PropertiesConfig initialized");
		} catch (TSACommonException e) {
			System.out.println("TSA Config load failed : " + e.getMessage());
		}

		try {
			PdfSignerParameter param = new PdfSignerParameter(config);
			
			PdfSigner pdfSigner = new PdfSignerImpl(param);
			System.out.println("Pdf signer initialized");
			
			pdfSigner.signPdf(pdfPath, outPdfPath);

			File outPdfFile = new File(outPdfPath);
			
			if (outPdfFile.exists()) {
				System.out.println("TSA Signed Success");
			} 
		} catch (PdfSignerException e) {
			System.out.println("Sign failed : PdfSignerException " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Sign failed : Exception : " + e);
			e.printStackTrace();
		}
		
	}
}
