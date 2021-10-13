package com.mobileleader.edoc.exception;

public enum EdocError {

	/* Common : 999
	 * AbstractEdocAction : 001
	 * CreatePdfAction : 002
	 * CreateTsaAction : 003
	 * CreateImageAction : 004
	 * SendEcmAction : 005
	 */
	PDF_CREATE_ERROR("E00201", "Pdf Create Error"),
	PDF_CREATE_MODULE_ERROR("E00202", "Pdf Moudule Error"),
	XML_FILE_NOT_FOUND("E00203", "XML File Not Found"),

	TSA_CREATE_ERROR("E00301", "TSA PDF Create Error"),
	TSA_CREATE_MODULE_ERROR("E00302", "TSA PDF Create Module Error"),
	TSA_CONFIG_FILE_LOAD_ERROR("E00303", "TSA Config File Load Error"),
	PDF_FILE_NOT_FOUND("E00304", "PDF File Not Found"),
	
	IMG_CREATE_ERROR("E00401", "Image Create Error"),
	IMG_CREATE_MODULE_ERROR("E00402", "Image Create Module Error"),
	IMG_CONVERT_MODULE_ERROR("E00403", "Image Convert Error"),
	IMG_MERGE_MODULE_ERROR("E00404", "Image Merge Error"),
	IMG_PAGE_COUNT_ERROR("E00405", "Image Page Count Error"),
	
	ECM_FILE_NOT_FOUND("E00501", "File Not Found"),
	ECM_SEND_ERROR("E00502", "ECM Send Error"),
	ECM_INFO_REGIST_ERROR("E00503", "Ecm Info Regist Error"),
	ECM_MAINKEY_NOT_FOUND("E00504", "Ecm Main Key Not Found"),
	
	SQL_ERROR("E99901", "SQL Error"),
	FILE_NOT_FOUND("E99902", "File Not Found"),
	FILE_DELETE_ERROR("E99903", "File Delete Error")
    ;
	
    
    private final String code;

    private final String message;

    EdocError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
