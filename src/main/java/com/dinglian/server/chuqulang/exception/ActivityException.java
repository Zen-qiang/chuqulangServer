package com.dinglian.server.chuqulang.exception;

@SuppressWarnings("serial")
public class ActivityException extends Exception {

	public final static int NOT_EXISTING = 2001;

	public final static int TOPIC_EXISTING = 2002;
	
	public final static int TOPIC_NOT_EXISTING = 2003;
	
	private int code;

	private static String getMessage(int code) {
		switch (code) {
		case NOT_EXISTING:
			return "活动不存在";
		case TOPIC_EXISTING:
			return "活动群聊已存在";
		case TOPIC_NOT_EXISTING:
			return "活动群聊不存在";
		default:
			return null; // cannot be
		}
	}

	public int getCode() {
		return code;
	}

	public ActivityException(int code) {
		super(getMessage(code));
		this.code = code;
	}

}
