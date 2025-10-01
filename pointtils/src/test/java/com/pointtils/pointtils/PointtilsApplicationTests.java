package com.pointtils.pointtils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@EnableAutoConfiguration(exclude = S3AutoConfiguration.class)
class PointtilsApplicationTests {

	@MockitoBean
	private S3Client s3Client;

	@Test
	void contextLoads() {
	}

}
