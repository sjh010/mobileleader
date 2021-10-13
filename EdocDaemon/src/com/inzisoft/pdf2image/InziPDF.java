//======================================================
/* Copyright (c) 1999-2015 INZISOFT Co., Ltd. All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited 
 * All information contained herein is, and remains the property of INZISOFT Co., Ltd.
 * The intellectual and technical concepts contained herein are proprietary to INZISOFT Co., Ltd.
 * Dissemination of this information or reproduction of this material is 
 * strictly forbidden unless prior written permission is obtained from INZISOFT Co., Ltd.
 * Proprietary and Confidential.
 */
//======================================================

package com.inzisoft.pdf2image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class InziPDF {
	public static final int SEARCH_INFO_SIZE = 6;
	public static final int MATRIX_SIZE = 6;
	public static final int TRANSFORM_BOX_SIZE = 4;

	public class PDFText {
		public String text;
		public int x0;
		public int y0;
		public int x1;
		public int y1;
	}

	/* load our native library */
	static {
		String path = System.getProperty("user.dir").replace("\\", "/") + "/Config/Config.properties";
		System.out.println("Config.properties file path = " + path);

		FileInputStream fis;
		try {
			
			fis = new FileInputStream(path);
			Properties properties = new Properties();
			properties.load(fis);
			
			String PDF2IMAGE_SO_FILE = (String) properties.getProperty("pdfso_path");
			String LICENSE_FILE = (String) properties.getProperty("LicenseFile");
			
			System.load(PDF2IMAGE_SO_FILE);
			loadLicenseFile(LICENSE_FILE);
			
			System.out.println("Config.properties pdfso_path = " + PDF2IMAGE_SO_FILE);
			System.out.println("Config.properties LicenseFile = " + LICENSE_FILE);
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}

		// 설치할 경로에 맞게 변경 필요

	}

	/* The native functions */

	/**
	 * 하위 모듈의 경로 설정하는 함수(AIX에서사용, 다른 플랫폼은 사용 안해도 됨)
	 * 
	 * @param modulepath
	 *            [in] PDF2Image, 이미지 코덱 등이 있는 패스(풀경로)
	 * @return 1 : 성공 그외 : 실패
	 */
	public static native int SetP2ILibraryPath(String modulepath);

	/**
	 * 전체 페이지 수 가져오기
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @return 전체 페이지 수<br/>
	 *         0: 실패<br/>
	 */
	public static native int getPDFPageCount(String filenameSrc);

	/**
	 * 전체 페이지를 DPI비율에 따라 이미지로 변환
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param folderDst
	 *            [in] 이미지 생성 폴더
	 * @param resolution
	 *            [in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate
	 *            [in] 압축을 사용하는 포맷의 압축 값<br/>
	 *            JPEG, JPEG in TIFF: 1 ~ 100<br/>
	 *            JPEG2000, JPEG2000 in TIFF: 24 - 1bpp, 12 - 2bpp, 48 - 0.5bpp<br/>
	 * @param filetype
	 *            [in] 저장할 파일 형식 설정<br/>
	 *            1: BMP<br/>
	 *            2: JPEG<br/>
	 *            3: JPEG2000<br/>
	 *            4: TIFF<br/>
	 * @param comptype
	 *            [in] filetype이 TIFF일 때 압축 방식 설정<br/>
	 *            1: 무압축<br/>
	 *            2: RLE<br/>
	 *            3: G3<br/>
	 *            4: G4<br/>
	 *            5: LZW<br/>
	 *            7: JPEG<br/>
	 *            34713: JPEG2000<br/>
	 *            34663: JBIG2<br/>
	 * @param binarize
	 *            [in] 이진화 여부 설정<br/>
	 *            0: 이진화 수행 안 함<br/>
	 *            그외: 이진화 수행<br/>
	 *            filetype이 1, 4일 사용 가능<br/>
	 *            filetype이 4이고, comptype이 3, 4, 34663 일 때 사용 가능<br/>
	 * @param threshold
	 *            [in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2Image(String filenameSrc, String folderDst, int resolution, int comprate,
			int filetype, int comptype, int binarize, int threshold);

	/**
	 * 전체 페이지를 DPI비율에 따라 이미지 및 inzi iFrom 형식의 썸네일로 변환
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param folderDst
	 *            [in] 이미지 생성 폴더
	 * @param resolution
	 *            [in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate
	 *            [in] 압축을 사용하는 포맷의 압축 값, 상세 내용은 convertPDF2Image 참조
	 * @param filetype
	 *            [in] 저장할 파일 형식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param comptype
	 *            [in] filetype이 TIFF일 때 압축 방식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param binarize
	 *            [in] 이진화 여부 설정, 상세 내용은 convertPDF2Image 참조
	 * @param threshold
	 *            [in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @param makeizt
	 *            [in] InziForm 형식의 썸네일 이미지를 생성 설정<br/>
	 *            0: 생성 안 함<br/>
	 *            그외: 생성<br/>
	 * @param thumbnailWid
	 *            [in] makeizt가 0이 아닐 때 생성되는 썸네일의 가로 길이
	 * @param thumbnailHgt
	 *            [in] makeizt가 0이 아닐 때 생성되는 썸네일의 세로 길이
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2ImageWithThumbnail(String filenameSrc, String folderDst, int resolution,
			int comprate, int filetype, int comptype, int binarize, int threshold, int makeizt, int thumbnailWid,
			int thumbnailHgt);

	/**
	 * 특정 페이지를 DPI비율에 따라 이미지로 변환
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param folderDst
	 *            [in] 이미지 생성 폴더
	 * @param resolution
	 *            [in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate
	 *            [in] 압축을 사용하는 포맷의 압축 값, 상세 내용은 convertPDF2Image 참조
	 * @param filetype
	 *            [in] 저장할 파일 형식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param comptype
	 *            [in] filetype이 TIFF일 때 압축 방식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param binarize
	 *            [in] 이진화 여부 설정, 상세 내용은 convertPDF2Image 참조
	 * @param threshold
	 *            [in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @param page
	 *            [in] 변환할 이미지의 페이지 번호 (0-base)
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2ImageOnePage(String filenameSrc, String folderDst, int resolution,
			int comprate, int filetype, int comptype, int binarize, int threshold, int page); // page
																								// =
																								// 0-based

	/**
	 * 특정 페이지를 DPI비율에 따라 이미지 및 inzi iFrom 형식의 썸네일로 변환
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param folderDst
	 *            [in] 이미지 생성 폴더
	 * @param resolution
	 *            [in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate
	 *            [in] 압축을 사용하는 포맷의 압축 값, 상세 내용은 convertPDF2Image 참조
	 * @param filetype
	 *            [in] 저장할 파일 형식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param comptype
	 *            [in] filetype이 TIFF일 때 압축 방식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param binarize
	 *            [in] 이진화 여부 설정, 상세 내용은 convertPDF2Image 참조
	 * @param threshold
	 *            [in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @param page
	 *            [in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param makeizt
	 *            [in] InziForm 형식의 썸네일 이미지를 생성 설정<br/>
	 *            0: 생성 안 함<br/>
	 *            그외: 생성<br/>
	 * @param thumbnailWid
	 *            [in] makeizt가 0이 아닐 때 생성되는 썸네일의 가로 길이
	 * @param thumbnailHgt
	 *            [in] makeizt가 0이 아닐 때 생성되는 썸네일의 세로 길이
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2ImageOnePageWithThumbnail(String filenameSrc, String folderDst, int resolution,
			int comprate, int filetype, int comptype, int binarize, int threshold, int page, int makeizt,
			int thumbnailWid, int thumbnailHgt);

	/**
	 * 특정 페이지를 Inzi iForm 형식의 썸네일 이미지로 변환
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param folderDst
	 *            [in] 이미지 생성 폴더
	 * @param page
	 *            [in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param thumbnailWid
	 *            [in] 썸네일의 가로 길이
	 * @param thumbnailHgt
	 *            [in] 썸네일의 세로 길이
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2ThumbnailOnePage(String filenameSrc, String folderDst, int page,
			int thumbnailWid, int thumbnailHgt);

	/**
	 * 특정 페이지를 메모리 형태의 raw이미지로 변환 했을 때 크기를 계산
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param page
	 *            [in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param sizemode
	 *            [in] 이미지 크기 지정 방식<br/>
	 *            1: DPI모드<br/>
	 *            2: Pixel모드<br/>
	 * @param size
	 *            [in] 이미지 크기의 값<br/>
	 *            sizemode가 1일 경우: 100, 200, 300등 DPI값<br/>
	 *            sizemode가 2일 경우: 3509등 pixel값<br/>
	 * @param datamode
	 *            [in] raw 이미지의 형식<br/>
	 *            0: RGBA8888<br/>
	 *            1: RGB565<br/>
	 *            2: RGB<br/>
	 *            3: DIB<br/>
	 * @param aDataInfo
	 *            [out] 결과 값<br/>
	 *            aDataInfo[0]: raw데이터의 크기<br/>
	 *            aDataInfo[1]: raw데이터의 가로 길이<br/>
	 *            aDataInfo[2]: raw데이터의 세로 길이<br/>
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2DataApprox(String filenameSrc, int page, int sizemode, int size, int datamode,
			long[] aDataInfo);

	/**
	 * 특정 페이지를 DPI비율에 따라 메모리 형태의 raw이미지로 변환
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param page
	 *            [in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param sizemode
	 *            [in] 이미지 크기 지정 방식<br/>
	 * @param size
	 *            [in] 이미지 크기의 값<br/>
	 * @param datamode
	 *            [in] raw 이미지의 형식<br/>
	 * @param outputdata
	 *            [out] 변환된 raw이미지가 저장 될 메모리
	 * @param aDataInfo
	 *            [in] convertPDF2DataApprox에서 반환된 array
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int convertPDF2DataOnePageWithSizeMode(String filenameSrc, int page, int sizemode, int size,
			int datamode, byte[] outputdata, long[] aDataInfo);

	/**
	 * 외부 폰트 불러오기
	 * 
	 * @param filename
	 *            [in] 외부 폰트 파일 리스트 *
	 */
	public static native void loadExternalFontList(String filename);

	/**
	 * 서버 라이선스 파일의 경로 지정
	 * 
	 * @param filename
	 *            [in] 라이선스 파일의 절대 경로
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int loadLicenseFile(String filename);

	/**
	 * 특정페이지를 PDF로 추출
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param filenameDst
	 *            [in] 추출될 파일 이름
	 * @param page
	 *            [in] 추출할 페이지 번호 (1-base)
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int extractPDFPage(String filenameSrc, String filenameDst, int page);

	/**
	 * 특정페이지들을 PDF로 추출
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param filenameDst
	 *            [in] 추출될 파일 이름
	 * @param extractPages
	 *            [in] 추출할 페이지 번호들(1-base) - 각 페이지 번호는 구분자로 구분 - 페이지 순서는 입력된 페이지
	 *            번호순과 동일
	 * @param delimiter
	 *            [in] 페이지 구분자
	 * @param skipStreamComparison
	 *            [in] PDF stream 비교를 스킵 할 지 여부
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int extractPDFPages(String filenameSrc, String filenameDst, String extractPages,
			int delimiter, int skipStreamComparison);

	/**
	 * PDF의 특정페이지들을 제거
	 * 
	 * @param filenameSrc
	 *            [in] 파일 이름
	 * @param filenameDst
	 *            [in] 추출될 파일 이름
	 * @param extractPages
	 *            [in] 제거할 페이지 번호들(1-base) - 각 페이지 번호는 구분자로 구분
	 * @param delimiter
	 *            [in] 페이지 구분자
	 * @param skipStreamComparison
	 *            [in] PDF stream 비교를 스킵 할 지 여부
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int removePDFPages(String filenameSrc, String filenameDst, String removePages, int delimiter,
			int skipStreamComparison);

	/**
	 * PDF 파일 로딩
	 * 
	 * @param filename
	 *            [in] PDF 파일 명
	 * @param password
	 *            [in] PDF 파일 암호
	 * @param extractText
	 *            [in] 텍스트 추출할 지 여부
	 * @param enableAnnotsManaging
	 *            [in] 주석 관리 기능 사용 여부
	 * @return 0 아님: 성공. PDF 문서 포인터<br/>
	 *         0: 실패<br/>
	 */
	public static native long LoadDocument(String filename, String password, int extractText, int enableAnnotsManaging);

	/**
	 * PDF 메모리 로딩
	 * 
	 * @param inputData
	 *            [in] PDF 메모리 버퍼
	 * @param inputLength
	 *            [in] PDF 메모리 버퍼의 길이
	 * @param password
	 *            [in] PDF 파일 암호
	 * @param extractText
	 *            [in] 텍스트 추출할 지 여부
	 * @param enableAnnotsManaging
	 *            [in] 주석 관리 기능 사용 여부
	 * @return 0 아님: 성공. PDF 문서 포인터<br/>
	 *         0: 실패<br/>
	 */
	public static native long LoadMemory(byte[] inputData, int inputLength, String password, int extractText,
			int enableAnnotsManaging);

	/**
	 * 전체 페이지 개수를 추출
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @return 1: 전체 페이지 수<br/>
	 *         0: 실패<br/>
	 */
	public static native int GetPageCount(long pdfdoc);

	/**
	 * 로드된 PDF문서 종료
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 */
	public static native void CloseDocument(long pdfdoc);

	/**
	 * 특정 페이지 로드
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 로드할 페이지 포인터 (0-base)
	 * @param reload
	 *            [in] 0 - reload 안함, 1 - reload, 1로 설정할 경우 해당 페이지가 로드 되어 있어도 다시
	 *            재로드
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int LoadPage(long pdfdoc, int page, int reload);

	/**
	 * 특정 로드된 페이지 해제
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native void FreePage(long pdfdoc, int pgae);

	/**
	 * 로드된 전체 페이지 해제
	 * 
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native void FreeAllPage(long pdfdoc);

	/**
	 * 가장 최근에 로드된 페이지의 가로 길이를 반환
	 * 
	 * @return 페이지 가로 길이<br/>
	 *         0.0 : 실패<br/>
	 */
	public static native float GetPageWidth(long pdfdoc);

	/**
	 * 가장 최근에 로드된 페이지의 세로 길이를 반환
	 * 
	 * @return 페이지 가로 길이<br/>
	 *         0.0 : 실패<br/>
	 */
	public static native float GetPageHeight(long pdfdoc);

	/**
	 * RenderPage를 위한 메모리 데이터 크기 계산
	 * 
	 * @param width
	 *            [in] 렌더링 될 가로 길이
	 * @param height
	 *            [in] 렌더링 될 세로 길이
	 * @param format
	 *            [in] raw 데이터 형식<br/>
	 *            0: RGBA8888<br/>
	 *            1: RGB565<br/>
	 *            2: RGB<br/>
	 *            3: DIB<br/>
	 * @return 렌더링 될 메모리 데이터 크기<br/>
	 *         0: 실패<br/>
	 */
	public static native int CalculateRenderPageSize(int width, int height, int format);

	/**
	 * 로드된 페이지를 이미지로 변환
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param data
	 *            [in] 렌더링 된 이미지 저장 버퍼
	 * @param dataSize
	 *            [in] 렌더링 이미지 버퍼 크기
	 * @param width
	 *            [in] 렌더링 가로 크기
	 * @param height
	 *            [in] 렌더링 세로 크기
	 * @param patchX
	 *            [in] ROI적용 시 ROI 좌상당 x좌표
	 * @param patchY
	 *            [in] ROI적용 시 ROI 좌상당 y좌표
	 * @param patchW
	 *            [in] ROI적용 시 ROI 가로 길이
	 * @param patchH
	 *            [in] ROI적용 시 ROI 세로 길이
	 * @param format
	 *            [in] raw 데이터 형식<br/>
	 *            0: RGBA8888<br/>
	 *            1: RGB565<br/>
	 *            2: RGB<br/>
	 *            3: DIB<br/>
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int RenderPage(long pdfdoc, byte[] data, int dataSize, int width, int height, int patchX,
			int patchY, int patchW, int patchH, int format);

	/**
	 * 특정 페이지에서 추출될 텍스트 객체의 크기 가져오기
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @return PDFText객체의 크기<br/>
	 *         0이하의 음수: 실패<br/>
	 */
	public static native int GetTextArraySize(long pdfdoc, int page);

	/**
	 * 특정 페이지에서 텍스트 정보 추출
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @param textArray
	 *            [out] 추출된 텍스트 정보 (배열의 각 PDFText 객체는 new로 할당된 상태이어야 함)
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int GetTextArray(long pdfdoc, int page, PDFText[] textArray);

	/**
	 * PDF내의 텍스트 검색
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param searchedPage
	 *            [out] 각 페이지별 텍스트 검색 여부 (데이터의 크기 >= 페이즈 수)
	 * @param startPage
	 *            [in] 검색을 시작하고자 하는 페이지 번호 (0-base)
	 * @param startPos
	 *            [in] 검색 시작 텍스트의 위치 (-1 : 최초 검색 시)
	 * @param direction
	 *            [in] 검색 방향 (1: 순방향, 0: 역방향)
	 * @param text
	 *            [in] 검색할 텍스트
	 * @param searchedInfo
	 *            [out] 검색된 텍스트 정보<br/>
	 *            배열의 크기는 SEARCH_INFO_SIZE 값 사용<br/>
	 *            searchedInfo[0] : 검색된 페이지 번호<br/>
	 *            searchedInfo[1] : 검색된 텍스트 위치<br/>
	 *            searchedInfo[2] : 검색된 텍스트의 x0 좌표<br/>
	 *            searchedInfo[3] : 검색된 텍스트의 y0 좌표<br/>
	 *            searchedInfo[4] : 검색된 텍스트의 x1 좌표<br/>
	 *            searchedInfo[5] : 검색된 텍스트의 y1 좌표<br/>
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int TextSearch(long pdfdoc, byte[] searchedPage, int startPage, int startPos, int direction,
			String text, long[] searchedInfo);

	/**
	 * 특정 페이지의 주석데이터 크기 가져오기
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @return 주석데이터의 크기<br/>
	 *         0이하의 음수: 실패<br/>
	 */
	public static native int GetAnnotationSize(long pdfdoc, int page);

	/**
	 * 특정 페이지의 주석데이터 가져오기
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @param data
	 *            [out] 주석데이터
	 * @param dataSize
	 *            [in] 주석데이터 크기
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int GetAnnotation(long pdfdoc, int page, byte[] data, int dataSize);

	/**
	 * 특정 페이지의 주석데이터 삽입하기
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @param data
	 *            [in] 삽입할 주석데이터
	 * @param dataSize
	 *            [in] 삽입할 주석데이터 크기
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int SetAnnotation(long pdfdoc, int page, byte[] data, int dataSize);

	/**
	 * 특정 페이지의 주석데이터 삭제
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 페이지 번호 (0-base)
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int RemoveAnnotation(long pdfdoc, int page);

	/**
	 * 주석데이터 처리된 특정페이지 저장
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param filename
	 *            [in] 저장될 파일 이름
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int SaveAnnotation(long pdfdoc, String filename);

	/**
	 * 가장 최근에 렌더링된 PDF 페이지의 matrix 정보 가져오기
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param matrix
	 *            [out] matrix 정보<br/>
	 *            배열의 크기는 MATRIX_SIZE 값 사용<br/>
	 *            matrix[0] : matrix a값<br/>
	 *            matrix[1] : matrix b값<br/>
	 *            matrix[2] : matrix c값<br/>
	 *            matrix[3] : matrix d값<br/>
	 *            matrix[4] : matrix e값<br/>
	 *            matrix[5] : matrix f값<br/>
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int GetTransformMatrix(long pdfdoc, float[] matrix);

	/**
	 * matrix 정보를 통해 좌표를 변환, PDF좌표와 렌더링된 이미지 좌표간 변환에 사용
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param a
	 *            [in] matrix a값
	 * @param b
	 *            [in] matrix b값
	 * @param c
	 *            [in] matrix c값
	 * @param d
	 *            [in] matrix d값
	 * @param e
	 *            [in] matrix e값
	 * @param f
	 *            [in] matrix f값
	 * @param oriX0
	 *            [in] 좌표 x0
	 * @param oriY0
	 *            [in] 좌표 y0
	 * @param oriX1
	 *            [in] 좌표 x1
	 * @param oriY1
	 *            [in] 좌표 y1
	 * @param transformData
	 *            [out] 변환된 좌표<br/>
	 *            배열의 크기는 TRANSFORM_BOX_SIZE 값 사용<br/>
	 *            transformData[0] : 변환된 좌표 x0<br/>
	 *            transformData[1] : 변환된 좌표 y0<br/>
	 *            transformData[2] : 변환된 좌표 x1<br/>
	 *            transformData[3] : 변환된 좌표 y1<br/>
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int TransformBBox(float a, float b, float c, float d, float e, float f, int oriX0, int oriY0,
			int oriX1, int oriY1, long[] transformData);

	/**
	 * matrix 정보의 역변환을 통한 좌표를 변환, PDF좌표와 렌더링된 이미지 좌표간 변환에 사용
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param a
	 *            [in] matrix a값
	 * @param b
	 *            [in] matrix b값
	 * @param c
	 *            [in] matrix c값
	 * @param d
	 *            [in] matrix d값
	 * @param e
	 *            [in] matrix e값
	 * @param f
	 *            [in] matrix f값
	 * @param oriX0
	 *            [in] 좌표 x0
	 * @param oriY0
	 *            [in] 좌표 y0
	 * @param oriX1
	 *            [in] 좌표 x1
	 * @param oriY1
	 *            [in] 좌표 y1
	 * @param transformData
	 *            [out] 변환된 좌표<br/>
	 *            배열의 크기는 TRANSFORM_BOX_SIZE 값 사용<br/>
	 *            transformData[0] : 변환된 좌표 x0<br/>
	 *            transformData[1] : 변환된 좌표 y0<br/>
	 *            transformData[2] : 변환된 좌표 x1<br/>
	 *            transformData[3] : 변환된 좌표 y1<br/>
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int InverseTransformBBox(float a, float b, float c, float d, float e, float f, int oriX0,
			int oriY0, int oriX1, int oriY1, long[] transformData);

	/**
	 * PDF 파일에서 이미지 추출
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 추출될 페이지 번호 (0-base), 음수일 경우 전체 이미지 추출
	 * @param prefix
	 *            [in] 생성될 이미지 파일의 prefix
	 * 
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int GetImages(long pdfdoc, int page, String prefix);

	/**
	 * PDF 파일에서 폰트 추출
	 * 
	 * @param pdfdoc
	 *            [in] PDF 문서 포인터
	 * @param page
	 *            [in] 추출될 페이지 번호 (0-base), 음수일 경우 전체 폰트 추출
	 * @param prefix
	 *            [in] 생성될 폰트 파일의 prefix
	 * 
	 * @return 1: 성공<br/>
	 *         그외: 실패<br/>
	 */
	public static native int GetFonts(long pdfdoc, int page, String prefix);

	public InziPDF() {
	}

	public static native int mergeTIFF(String singlePageListStr, int paramInt, String mergeFileName);

	// public static int getTotalPageTiffImages(String strPDFFile, String
	// strImageFolder, int resolution, int comprate,
	// int filetype, int comptype, int binarize, int threshold)
	// {
	// if(resolution == 0)
	// resolution = 144;
	//
	// File dir = new File(strImageFolder);
	// dir.mkdir();
	//
	// //2012-1-9 수정
	// //int convertPages = convertPDF2Image(strPDFFile, strImageFolder,
	// resolution, comprate);
	//
	//
	// //2012-1-28수정///////////////////////////////////////
	// int totalPage = getPDFPageCount(strPDFFile);
	// System.out.println("hhshin totalPage :" + totalPage);
	//
	// ConvertPageThread [] cTh = new ConvertPageThread[totalPage];
	//
	// for(int i = 0 ; i < totalPage ; i++)
	// {
	// cTh[i] = new ConvertPageThread(strPDFFile, strImageFolder, resolution,
	// comprate,
	// filetype, comptype, binarize, threshold, i);
	// cTh[i].start();
	// //System.out.println("hhshin thread start :" + i);
	// }
	//
	// for(int i = 0 ; i < totalPage ; i++)
	// {
	// try {
	// cTh[i].join();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return totalPage;
	// }
	//
	// public static int getTotalPageTiffImagesWithThumbnail(String strPDFFile,
	// String strImageFolder, int resolution, int comprate,
	// int filetype, int comptype, int binarize, int threshold,
	// int makeizt, int thumbnailWid, int thumbnailHgt)
	// {
	// if(resolution == 0)
	// resolution = 144;
	//
	// File dir = new File(strImageFolder);
	// dir.mkdir();
	//
	// //2012-1-9 수정
	// //int convertPages = convertPDF2Image(strPDFFile, strImageFolder,
	// resolution, comprate);
	//
	//
	// //2012-1-28수정///////////////////////////////////////
	// int totalPage = getPDFPageCount(strPDFFile);
	// System.out.println("totalPage :" + totalPage);
	//
	// ConvertPageThread [] cTh = new ConvertPageThread[totalPage];
	//
	// for(int i = 0 ; i < totalPage ; i++)
	// {
	// cTh[i] = new ConvertPageThread(strPDFFile, strImageFolder, resolution,
	// comprate,
	// filetype, comptype, binarize, threshold, i,
	// makeizt, thumbnailWid, thumbnailHgt);
	// cTh[i].start();
	//
	// try {
	// Thread.sleep(200);
	// } catch(InterruptedException e){
	// System.out.println(e.getMessage());
	// }
	//
	// //System.out.println("thread start :" + i);
	// }
	//
	// for(int i = 0 ; i < totalPage ; i++)
	// {
	// try {
	// cTh[i].join();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return totalPage;
	// }
	//
	// public static int getTotalPageThumbnail(String strPDFFile, String
	// strImageFolder, int thumbnailWid, int thumbnailHgt)
	// {
	// File dir = new File(strImageFolder);
	// dir.mkdir();
	//
	// int totalPage = getPDFPageCount(strPDFFile);
	// System.out.println("totalPage :" + totalPage);
	//
	// ConvertPageThread [] cTh = new ConvertPageThread[totalPage];
	//
	// for(int i = 0 ; i < totalPage ; i++)
	// {
	// cTh[i] = new ConvertPageThread(strPDFFile, strImageFolder, i,
	// thumbnailWid, thumbnailHgt);
	// cTh[i].start();
	// //System.out.println("thread start :" + i);
	// }
	//
	// for(int i = 0 ; i < totalPage ; i++)
	// {
	// try {
	// cTh[i].join();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return totalPage;
	// }
}
