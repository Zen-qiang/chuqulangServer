package com.dinglian.server.chuqulang.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://wx.qlogo.cn/mmopen/VLjDyM7pibyK12nfJuWo8VIIR9mYXomlsUGlXGD3q5o8taPITKleBqv8zCj5kxpicgazf2f3Psn7ibbTxZcSoB59Q/0");  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        conn.setRequestMethod("GET");  
        conn.setConnectTimeout(5 * 1000);  
        InputStream inStream = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
        	baos.write(buffer, 0, len);  
        }  
        inStream.close();  
        byte[] data = baos.toByteArray();
        File imageFile = new File("E:/BeautyGirl.jpg");  
        FileOutputStream fos = new FileOutputStream(imageFile);  
        fos.write(data);  
        fos.close();  
	}
	
	public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }  

}
