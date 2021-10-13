package com.mobileleader.edoc.type;

/**
 * 전자문서 처리 상태 코드
 */
public enum ProcessStatusCode {

	ONGOING("0", "진행중"),
	SUCCESS("1", "성공"), 
	FAIL("9", "실패"),;

	private String code;

	private String message;

	private ProcessStatusCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public static ProcessStatusCode getByCode(String code) {
		for (ProcessStatusCode value : ProcessStatusCode.values()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		return null;
	}
}
