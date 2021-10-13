package com.mobileleader.edoc.type;

/**
 * 이미지 서버 전송: 이미지 타입 코드
 */
public enum DocType {

	PDF("PDF", "edoc"), IMAGE("IMAGE", "edoc");

	private String type;

	private String channel;

	private DocType(String type, String channel) {
		this.type = type;
		this.channel = channel;
	}

	public String getType() {
		return type;
	}

	public String getChannel() {
		return channel;
	}

	public static DocType getByType(String type) {
		for (DocType value : DocType.values()) {
			if (value.getType().equals(type)) {
				return value;
			}
		}
		return null;
	}
}
