package com.dinglian.server.chuqulang.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class EncryptHelper {
	
	public static String encryptByMD5(String username, String password){
		String algorithmName = "MD5";
		Object salt = ByteSource.Util.bytes(username);;
		int hashIterations = 1024;
		SimpleHash result = new SimpleHash(algorithmName, password, salt, hashIterations);
		return result.toString();
	}
	
	public static void main(String[] args) {
//		System.out.println(encryptByMD5("fyunxu", "123456"));
	}
}
