package org.hdstart.cloud.async;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@Slf4j
public class VideoThumbnailService {

    /**
     * 从MultipartFile视频生成缩略图，仅支持MP4和MOV格式
     * @param videoFile 上传的视频文件
     * @param timeInSeconds 截取缩略图的时间点(秒)
     * @param imageFormat 输出图片格式(jpg, png等)
     * @return 缩略图的字节数组
     */
    public byte[] generateThumbnail(MultipartFile videoFile, int timeInSeconds, String imageFormat) throws Exception {
        // 验证输入
        if (videoFile.isEmpty()) {
            log.error("视频文件不能为空");
        }

        // 验证视频格式是否为MP4或MOV
        if (!isAllowedVideoFormat(videoFile.getContentType(), videoFile.getOriginalFilename())) {
            log.error("只支持MP4和MOV格式的视频文件");
        }

        FFmpegFrameGrabber grabber = null;
        InputStream inputStream = null;

        try {
            // 从MultipartFile获取输入流
            inputStream = videoFile.getInputStream();

            // 创建视频抓取器
            grabber = new FFmpegFrameGrabber(inputStream);
            grabber.start();

            // 获取视频总时长(秒)
            long totalTime = grabber.getLengthInTime();
            int totalSeconds = totalTime > 0 ? (int) (totalTime / 1000000) : 0;

            // 确保截取时间在视频时长范围内
            if (totalSeconds > 0) {
                timeInSeconds = Math.min(Math.max(0, timeInSeconds), totalSeconds);
            } else {
                timeInSeconds = 0; // 如果无法获取时长，默认取第一帧
            }

            // 跳转到指定时间点
            grabber.setTimestamp(timeInSeconds * 1000000L); // 单位是微秒

            // 获取该时间点的帧
            Frame frame = grabber.grabImage();
            if (frame == null) {
                // 如果指定时间点无法获取帧，尝试获取第一帧
                grabber.setTimestamp(0);
                frame = grabber.grabImage();
                if (frame == null) {
                    log.error("无法从视频中获取任何帧");
                }
            }

            // 转换为BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.getBufferedImage(frame);

            // 将图片写入字节数组输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, imageFormat, outputStream);

            return outputStream.toByteArray();

        } finally {
            // 释放资源
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 验证是否为允许的视频格式(MP4或MOV)
     * 同时检查Content-Type和文件扩展名，提高准确性
     */
    public boolean isAllowedVideoFormat(String contentType, String originalFilename) {
        // 检查MIME类型
        boolean isAllowedContentType = "video/mp4".equals(contentType) ||
                "video/quicktime".equals(contentType);

        // 检查文件扩展名
        if (originalFilename != null && !originalFilename.isEmpty()) {
            String lowerFileName = originalFilename.toLowerCase();
            boolean isAllowedExtension = lowerFileName.endsWith(".mp4") ||
                    lowerFileName.endsWith(".mov");

            // 两种检查方式都通过才认为是允许的格式
            return isAllowedContentType && isAllowedExtension;
        }

        return isAllowedContentType;
    }
}
