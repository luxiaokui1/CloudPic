package com.yupi.yupicturebackend.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    /**
     * 域名（用于拼接可访问的图片 URL）
     */
    private String host;

    /**
     * secretId (MinIO access key)
     */
    private String secretId;

    /**
     * 密钥 (MinIO secret key)
     */
    private String secretKey;

    /**
     * 区域（MinIO 不需要，保留兼容）
     */
    private String region;

    /**
     * 桶名
     */
    private String bucket;

    /**
     * MinIO 服务端地址
     */
    private String endpoint;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(secretId, secretKey)
                .build();
    }
}
