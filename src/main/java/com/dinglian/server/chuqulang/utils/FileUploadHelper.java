package com.dinglian.server.chuqulang.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.dinglian.server.chuqulang.base.ApplicationConfig;

public class FileUploadHelper {

	public static String uploadProfilePicture(int userId, InputStream inputStream) throws IOException {
		ApplicationConfig config = ApplicationConfig.getInstance();
		String path = config.getUserProfilePicturePath();
		path = String.format(path, userId);
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		File file = new File(folder, "avatar.png");
    	BufferedImage im = ImageIO.read(inputStream);
    	ImageIO.write(im, "png", file);
		return file.getAbsolutePath();
	}

}
