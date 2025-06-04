package org.hdstart.cloud.utils.minio.utils;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.utils.minio.config.MinioConfig;
import org.hdstart.cloud.utils.minio.fileType.FileType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioUtils {

    private final MinioConfig config;

    private MinioClient client;

    @PostConstruct
    public void init() {
        client = MinioClient.builder()
                .endpoint(config.getEndpoint())
                .credentials(config.getAccessKey(), config.getSecretKey())
                .build();
    }

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, FileType fileType) {
        // 1. 当前日期路径（如 2025/05/27）
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 2. 原始文件名 + UUID 防止重名
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replace("-", "");

        String fileName = datePath + "/" + fileType.getType() + "/" + uuid + "-" + originalFilename;

        try {
            ensureBucketExists(config.getBucketName());

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(config.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("上传失败", e);
        }
    }

    /**
     * 获取文件预览地址（公网可访问）
     */
    public String getPreviewUrl(String objectName) {
        return config.getEndpoint() + "/" + config.getBucketName() + "/" + objectName;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(config.getBucketName())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("删除失败", e);
        }
    }

    /**
     * 确保桶存在
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}

