package com.dinglian.server.chuqulang.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.dinglian.server.chuqulang.base.ApplicationConfig;

public class AliyunOSSUtil {

	// private static final Logger logger =
	// LoggerFactory.getLogger(AliyunOSSUtil.class);

	private static final AliyunOSSUtil instance = new AliyunOSSUtil();

	private static final String endpoint;
	private static final String accessKeyId;
	private static final String accessKeySecret;
	private static final String bucketName;

	private static final String IMG_CONTENT_TYPE = "image/*";

	private OSSClient ossClient;

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

	private void openClient() {
		ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
	}

	private void closeClient() {
		if (ossClient != null) {
			ossClient.shutdown();
		}
	}

	private String putObject(PutObjectRequest putObjectRequest) {
		String uri = null;
		openClient();

		ossClient.putObject(putObjectRequest);

		OSSObject obj = ossClient.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
		if (obj != null && obj.getResponse() != null) {
			uri = obj.getResponse().getUri();
		}

		closeClient();
		return uri;
	}

	public void deleteObject(String key) {
		openClient();
		boolean found = ossClient.doesObjectExist(bucketName, key);
		if (found) {
			ossClient.deleteObject(bucketName, key);
		}
		closeClient();
	}

	private String calculateMd5(byte[] bytes) {
		return BinaryUtil.toBase64String(BinaryUtil.calculateMd5(bytes));
	}

	public String upload(String key, CommonsMultipartFile file) {
		String uri = null;
		try {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(file.getSize());
			meta.setContentMD5(this.calculateMd5(file.getBytes()));
			meta.setContentType(IMG_CONTENT_TYPE);

			uri = this.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), meta));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uri;
	}

	public String upload(String key, byte[] data) {
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(data.length);
		meta.setContentMD5(this.calculateMd5(data));
		meta.setContentType(IMG_CONTENT_TYPE);

		return this.putObject(new PutObjectRequest(bucketName, key, new ByteArrayInputStream(data), meta));
	}

	public String upload(String key, InputStream in) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, in);
		return this.putObject(putObjectRequest);
	}
}
