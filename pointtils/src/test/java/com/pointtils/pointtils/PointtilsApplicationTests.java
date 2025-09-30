package com.pointtils.pointtils;

import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;
import com.pointtils.pointtils.src.infrastructure.configs.TestEmailConfiguration;

@SpringBootTest
@EnableAutoConfiguration(exclude = S3AutoConfiguration.class)
@Import(TestEmailConfiguration.class)
class PointtilsApplicationTests {

	@MockitoBean
	private S3Client s3Client;

	@Test
	void contextLoads() {
	}

}
