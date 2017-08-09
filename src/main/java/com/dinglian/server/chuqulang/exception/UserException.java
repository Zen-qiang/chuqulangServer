package com.dinglian.server.chuqulang.exception;

@SuppressWarnings("serial")
public class UserException extends Exception {

	public final static int PHONE_NO_INVALID = 1001;
	public final static int PHONE_NO_REGISTERED = 1002;
	public final static int VERIFY_CODE_INVALID = 1003;
	public final static int VERIFY_CODE_EXPIRE = 1004;
	public final static int NOT_REGISTER = 1005;
	public final static int NOT_EXISTING = 1006;
	
	private int code;

	private static String getMessage(int code) {
		switch (code) {
		case PHONE_NO_INVALID:
			return "请输入正确的手机号码";
		case PHONE_NO_REGISTERED:
			return "该手机号已经注册";
		case VERIFY_CODE_INVALID:
			return "验证码无效，请重新获取";
		case VERIFY_CODE_EXPIRE:
			return "验证码过期";
		case NOT_REGISTER:
			return "该用户未注册";
		case NOT_EXISTING:
			return "用户不存在";
		default:
			return null; // cannot be
		}
	}

	public int getCode() {
		return code;
	}

	public UserException(int code) {
		super(getMessage(code));
		this.code = code;
	}

}
