package com.programmers.heycake.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;

@TestConfiguration
public class S3MockConfig {

	private static final String BASE_URL = "http://localhost:8001";

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public S3Mock s3Mock() {
		return new S3Mock.Builder()
				.withPort(8001)
				.withInMemoryBackend()
				.build();
	}

	@Bean
	public AmazonS3 amazonS3(S3Mock s3Mock) {
		s3Mock.start();

		AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(BASE_URL, region);
		AmazonS3 client = AmazonS3ClientBuilder
				.standard()
				.withPathStyleAccessEnabled(true)
				.withEndpointConfiguration(endpoint)
				.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
				.build();

		client.createBucket(bucketName);
		return client;
	}
}