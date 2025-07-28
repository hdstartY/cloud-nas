package org.hdstart.cloud.utils.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "minio")
@Configuration
@Data
public class MinioConfig {

    private String preUrl;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
