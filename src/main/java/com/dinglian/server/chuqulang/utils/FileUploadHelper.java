package com.dinglian.server.chuqulang.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.dinglian.server.chuqulang.base.ApplicationConfig;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class FileUploadHelper {

	public static String uploadProfilePicture(String phoneNo, InputStream inputStream) throws IOException {
		ApplicationConfig config = ApplicationConfig.getInstance();
		String path = config.getUserProfilePath();
		path = String.format(path, phoneNo);
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		File file = new File(folder, "avatar.jpg");
		BufferedImage im = ImageIO.read(inputStream);
		ImageIO.write(im, "jpg", file);
		String filePath = file.getAbsolutePath();
		String separator = config.getResourceFolder().replaceAll("/", "").trim();
		String[] splitStr = filePath.split(separator);
		return splitStr[1];
	}

	public static void readLocalImage(HttpServletResponse response, String picture) throws IOException {
		response.setContentType("image/png");
		ServletOutputStream out = response.getOutputStream();
		InputStream in = new FileInputStream(picture);
		byte tmp[] = new byte[256];
		int i;
		while ((i = in.read(tmp)) != -1) {
			out.write(tmp, 0, i);
		}
		in.close();
		out.flush();
		out.close();
		response.flushBuffer();
	}

	public static String base64EncodeImg(File file) throws IOException {
		InputStream in = null;
		byte[] data = null;
		in = new FileInputStream(file);
		data = new byte[in.available()];
		in.read(data);
		in.close();
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(data);
	}

	public static String uploadPicture(String folderPath, String picBase64Str, String fileName) throws IOException {
		picBase64Str = picBase64Str.split(",")[1];

		BASE64Decoder decoder = new BASE64Decoder();
		File parentFolder = new File(folderPath);
		if (!parentFolder.exists()) {
			parentFolder.mkdirs();
		}
		File file = new File(parentFolder, fileName);
		FileOutputStream write = new FileOutputStream(file);
		byte[] decoderBytes = decoder.decodeBuffer(picBase64Str);
		write.write(decoderBytes);
		write.close();

		String filePath = file.getAbsolutePath();
		String separator = ApplicationConfig.getInstance().getResourceFolder().replaceAll("/", "").trim();
		String[] splitStr = filePath.split(separator);

		if (splitStr.length == 2) {
			return splitStr[1].replace("\\", "/");
		} else {
			return "";
		}
		/*
		 * BufferedImage bIMG = ImageIO.read(file); File formatFile = new
		 * File(file.getParentFile(), index + ".png"); ImageIO.write(bIMG,
		 * "png", formatFile); file.delete(); return formatFile;
		 */
	}

	public static String saveNetProfilePicture(String phoneNo, String imgUrl) throws IOException {
		URL url = new URL(imgUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		inStream.close();
		byte[] data = baos.toByteArray();

		ApplicationConfig config = ApplicationConfig.getInstance();
		String path = config.getUserProfilePath();
		path = String.format(path, phoneNo);
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		File imageFile = new File(folder, "avatar.jpg");
		FileOutputStream fos = new FileOutputStream(imageFile);
		fos.write(data);
		fos.close();
		
		String filePath = imageFile.getAbsolutePath();
		String separator = config.getResourceFolder().replaceAll("/", "").trim();
		String[] splitStr = filePath.split(separator);
		return splitStr[1];
	}
	
	public static String uploadToAliyunOSSFromNetwork(String imgUrl, String folder, String fileName) throws IOException {
		AliyunOSSUtil util = AliyunOSSUtil.getInstance();

		URL url = new URL(imgUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		inStream.close();
		byte[] data = baos.toByteArray();
		
		String key = folder + "/" + fileName;
		return util.upload(key, data);
	}
	
	public static String generateTempImageFileName(){
		return String.valueOf(new Date().getTime()) + ".jpg";
	}

}
