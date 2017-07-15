package com.dinglian.server.chuqulang.base;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		ApplicationConfig config = ApplicationConfig.getInstance();
		System.out.println(config.getResourceRoot() + config.getResourceProfileFolder() + "1" + config.getResourceProfilePictureFolder());
	}

}
