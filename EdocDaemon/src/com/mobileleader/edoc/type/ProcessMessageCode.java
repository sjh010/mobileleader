package com.mobileleader.edoc.type;

/**
 * 전자문서 처리 상태 메시지 코드
 */
public enum ProcessMessageCode {
	
    INITIAL("AAAAAA", "초기값"), 
    CONVERT_START("000000", "변환 시작"),
    FINISH("FINISH", "최종 완료"),
    RETRY_MAX("MAXERR", "재시도 횟수 초과"),
    CANCEL("CANCEL", "취소 처리 대상"),
    ;

    private String code;

    private String message;

    private ProcessMessageCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ProcessMessageCode getByCode(String code) {
        for (ProcessMessageCode value : ProcessMessageCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
