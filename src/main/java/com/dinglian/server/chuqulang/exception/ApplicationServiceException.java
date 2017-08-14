package com.dinglian.server.chuqulang.exception;

@SuppressWarnings("serial")
public class ApplicationServiceException extends RuntimeException {

	// 用户相关
	public final static int PHONE_NO_INVALID = 1001;
	public final static int PHONE_NO_REGISTERED = 1002;
	public final static int VERIFY_CODE_INVALID = 1003;
	public final static int VERIFY_CODE_EXPIRE = 1004;
	public final static int USER_NOT_REGISTER = 1005;
	public final static int USER_NOT_EXISTING = 1006;
	
	// 活动相关
	public final static int ACTIVITY_NOT_EXIST = 2001;
	public final static int ACTIVITY_TOPIC_EXIST = 2002;
	public final static int ACTIVITY_TOPIC_NOT_EXIST = 2003;
	
	// 圈子相关
	public final static int COTERIE_NOT_EXIST = 3001;
	
	// 话题相关
	public final static int TOPIC_EXIST = 4001;
	public final static int TOPIC_NOT_EXIST = 4002;
	
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
		case USER_NOT_REGISTER:
			return "该用户未注册";
		case USER_NOT_EXISTING:
			return "用户不存在";
		case ACTIVITY_NOT_EXIST:
			return "活动不存在";
		case ACTIVITY_TOPIC_EXIST:
			return "活动群聊已存在";
		case ACTIVITY_TOPIC_NOT_EXIST:
			return "活动群聊不存在";
		case COTERIE_NOT_EXIST:
			return "圈子不存在";
		default:
			return null; // cannot be
		}
	}

	public int getCode() {
		return code;
	}

	public ApplicationServiceException(int code) {
		super(getMessage(code));
		this.code = code;
	}

}
