package com.dinglian.server.chuqulang.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.dinglian.server.chuqulang.base.ApplicationConfig;

import sun.misc.BASE64Decoder;

public class AliyunOSSUtil {

	private static final AliyunOSSUtil instance = new AliyunOSSUtil();

	private static final String endpoint;
	private static final String accessKeyId;
	private static final String accessKeySecret;
	private static final String bucketName;

	static {
		ApplicationConfig config = ApplicationConfig.getInstance();
		endpoint = config.getAliyunOSSEndpoint();
		accessKeyId = config.getAliyunOSSAccessKeyId();
		accessKeySecret = config.getAliyunOSSAccessSecret();
		bucketName = config.getAliyunOSSBucketName();
	}

	public static AliyunOSSUtil getInstance() {
		return instance;
	}

	public String putObject(String key, String base64Str) {
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		try {
			// Base64字符串解码
			byte[] decoderBytes = new BASE64Decoder().decodeBuffer(base64Str);

			ObjectMetadata meta = new ObjectMetadata();
			// 设置上传文件长度
			meta.setContentLength(decoderBytes.length);
			// 设置上传MD5校验
			String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(decoderBytes));
			meta.setContentMD5(md5);
			// 设置上传内容类型
			meta.setContentType("image/*");

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,
					new ByteArrayInputStream(decoderBytes), meta);

			ossClient.putObject(putObjectRequest);

			OSSObject obj = ossClient.getObject(bucketName, key);
			if (obj != null && obj.getResponse() != null) {
				return obj.getResponse().getUri();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
		return null;
	}

	public void deleteObject(String key) {
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		boolean found = ossClient.doesObjectExist(bucketName, key);
		if (found) {
			ossClient.deleteObject(bucketName, key);
		}
		ossClient.shutdown();
	}

	public String putObject(String key, byte[] data) {
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		ObjectMetadata meta = new ObjectMetadata();
		// 设置上传文件长度
		meta.setContentLength(data.length);
		// 设置上传MD5校验
		String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(data));
		meta.setContentMD5(md5);
		// 设置上传内容类型
		meta.setContentType("image/*");

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,
				new ByteArrayInputStream(data), meta);

		ossClient.putObject(putObjectRequest);

		OSSObject obj = ossClient.getObject(bucketName, key);
		if (obj != null && obj.getResponse() != null) {
			return obj.getResponse().getUri();
		}
		ossClient.shutdown();
		return null;
	}
}
