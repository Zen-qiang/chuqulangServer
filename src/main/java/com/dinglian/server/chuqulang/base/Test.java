package com.dinglian.server.chuqulang.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.dinglian.server.chuqulang.utils.NeteaseIMUtil;

import sun.misc.BASE64Decoder;

public class Test {

	public static void main(String[] args) throws Exception {
		/*String imgStr = "";
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(imgStr);
		OutputStream out = new FileOutputStream(new File("D:/aa.png"));
		out.write(b);
		out.flush();
		out.close();*/
		
		/*String str = "E:\\Server\\app-test-tomcat\\webapps\\chuqulang-resource\\profile\\18270790997\\avatar.png";
		String[] a = str.split("chuqulang-resource");
		for (String string : a) {
			System.out.println(string);
		}*/
		// /profile/18270790999/avatar.png
//		NeteaseIMUtil.getInstance().getUinfos("[\"18270790999\"]");
//		NeteaseIMUtil.getInstance().updateUinfo("18270790999", null, "/profile/18270790999/avatar.png", null, null, null, null, null, null);
		
		String str = "\\activity\\44\\1.png";
		System.out.println(str.replace("\\", "/"));
		
		
	}

}
