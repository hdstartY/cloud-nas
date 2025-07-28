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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.lang.System.in;

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

    public String uploadPreFile(BufferedImage bufferedImage, String oriFileName, FileType fileType) throws IOException {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");

        String baseName = oriFileName.contains(".") ?
                oriFileName.substring(0, oriFileName.lastIndexOf('.')) : oriFileName;
        String fileName = datePath + "/" + fileType.getType() + "/" + uuid + "-" + baseName + ".jpg";

        // 将 ARGB 转换为 RGB，避免 jpg 写入失败
        BufferedImage rgbImage = new BufferedImage(
                bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        rgbImage.getGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            boolean written = ImageIO.write(rgbImage, "jpg", baos);
            if (!written) {
                throw new IOException("ImageIO 写入失败，可能是不支持的图片格式");
            }

            byte[] imageBytes = baos.toByteArray();
            if (imageBytes.length == 0) {
                throw new IOException("压缩后图片大小为 0，上传终止");
            }

            try (InputStream uploadStream = new ByteArrayInputStream(imageBytes)) {
                ensureBucketExists(config.getBucketName());

                client.putObject(
                        PutObjectArgs.builder()
                                .bucket(config.getBucketName())
                                .object(fileName)
                                .stream(uploadStream, imageBytes.length, -1)
                                .contentType("image/jpeg")
                                .build()
                );
                return fileName;
            }

        } catch (Exception e) {
            log.error("图片上传失败：" + oriFileName, e);
            throw new RuntimeException("上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传字节数组到MinIO
     */
    public String uploadVideoPre(byte[] data, String objectName, FileType fileType){
        // 1. 当前日期路径（如 2025/05/27）
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 2. 原始文件名 + UUID 防止重名
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 3. 替换原始文件名的后缀为 .jpg
        String baseName = objectName.contains(".") ?
                objectName.substring(0, objectName.lastIndexOf('.')) : objectName;

        String fileName = datePath + "/" + fileType.getType() + "/" + uuid + "-" + baseName + ".jpeg";

        // 上传文件
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(config.getBucketName())
                            .object(fileName)
                            .stream(inputStream, data.length, -1)
                            .contentType("image/jpeg")
                            .build()
            );
            return fileName;
        } catch (Exception e) {
            log.error("视频缩略图上传失败!!!");
            throw new RuntimeException("上传失败：" + e.getMessage(), e);
        }
    }


    /**
     * 获取文件预览地址（公网可访问）
     */
    public String getPreviewUrl(String objectName) {
        return config.getPreUrl() + "/" + config.getBucketName() + "/" + objectName;
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

