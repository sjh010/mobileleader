package com.mobileleader.edoc.type;

/**
 * 전자문서 처리 단계 코드
 */
public enum ProcessStepCode {
	
    EDOC_KEY_CREATE("00", "시작", 1), 
    INPUT_DATA_VALIDATE("10", "입력 데이터 검증", 2),
    PDF_CREATE("20", "PDF 생성", 3),
    TSA("30", "TAS 인증", 4),
    PDF_SEND("40", "PDF 공전소 전송", 5),
    PDF_CONVERT("50", "PDF 이미지 변환", 6),
    CANCEL("60", "취소거래", 7),
    ECM("70", "ECM 이미지 전송", 8),
    
    NONE("99", "없음", 9)
    ;

    private String code;

    private String description;
    
    private int order;

    private ProcessStepCode(String code, String description, int order) {
        this.code = code;
        this.description = description;
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public int getOrder() {
    	return order;
    }
    
    public static ProcessStepCode getByCode(String code) {
        for (ProcessStepCode value : ProcessStepCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
    
    public static ProcessStepCode getByOrder(int order) {
    	for (ProcessStepCode value : ProcessStepCode.values()) {
            if (value.order == (order)) {
                return value;
            }
        }
        return null;
    }
    
    public static ProcessStepCode getNextCode(String currentEdocProcessStepCode) {
    	int order = ProcessStepCode.getByCode(currentEdocProcessStepCode).getOrder();
    	
    	if (order <= 4) {
    		return getByOrder(order++);
    	} else if (order <= 8) {
    		return ProcessStepCode.ECM;
    	} else {
    		return ProcessStepCode.NONE;
    	}	
    }
    
    public boolean compareCode(String code) {
    	if (getCode().contentEquals(code)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
}
