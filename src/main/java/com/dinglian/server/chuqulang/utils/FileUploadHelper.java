package com.dinglian.server.chuqulang.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
		
		File file = new File(folder, "avatar.png");
    	BufferedImage im = ImageIO.read(inputStream);
    	ImageIO.write(im, "png", file);
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

	public static String uploadActivityPicture(String picturePath, String picBase64Str, int index) throws IOException {
		picBase64Str = picBase64Str.split(",")[1];
		
		BASE64Decoder decoder = new BASE64Decoder();
		File parentFolder = new File(picturePath);
		if (!parentFolder.exists()) {
			parentFolder.mkdirs();
		}
		File file = new File(parentFolder, index + ".png");
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
		/*BufferedImage bIMG = ImageIO.read(file);
		File formatFile = new File(file.getParentFile(), index + ".png");
	    ImageIO.write(bIMG, "png", formatFile);
	    file.delete();
	    return formatFile;*/
	}

}
