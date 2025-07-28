package org.hdstart.cloud.async;

import net.coobird.thumbnailator.Thumbnails;
import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.entity.Blog;
import org.hdstart.cloud.entity.Images;
import org.hdstart.cloud.mapper.BlogMapper;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.ImagesService;
import org.hdstart.cloud.utils.minio.fileType.FileType;
import org.hdstart.cloud.utils.minio.utils.MinioUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlogAsyncService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private ImagesService imagesService;

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void publishBlog(BlogFile blogFile) {
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogFile, blog);
        if (blog.getIsPublic() == 1) {
            blog.setIsPublic(2);
        }
        if (blog.getIsPublic() == 0) {
            blog.setIsPublic(3);
        }
        int bIsSuccess = blogMapper.insert(blog);
        if (bIsSuccess == 1 && blogFile.getImages() != null) {
            // 上传图片
            Map<String,String> objectNames = new HashMap<>();
            for (MultipartFile file : blogFile.getImages()) {
                String fileName = null;
                String oriName = null;
                try {
                    BufferedImage bufferedImage = Thumbnails.of(file.getInputStream()).size(120, 120).asBufferedImage();
                    oriName = minioUtils.uploadPreFile(bufferedImage, file.getOriginalFilename(), FileType.PRE_IMG);
                    fileName = minioUtils.uploadFile(file, FileType.BLOG);
                } catch (Exception e) {
                    removeImages(objectNames);
                    System.out.println(e.fillInStackTrace());
                    throw new RuntimeException("发布失败！");
                }
                objectNames.put(fileName,oriName);
            }

            //保存图片数据
            ArrayList<Images> storeImages = new ArrayList<>();
            objectNames.entrySet().stream().forEach(item -> {
                Images images = new Images();
                images.setBlogId(blog.getId());
                String previewUrl = minioUtils.getPreviewUrl(item.getKey());
                String oriviewUrl = minioUtils.getPreviewUrl(item.getValue());
                images.setPreUrl(previewUrl);
                images.setOriUrl(oriviewUrl);
                storeImages.add(images);
            });

            boolean iIsSuccess = imagesService.saveBatch(storeImages);
            if (!iIsSuccess) {
                throw new RuntimeException("发布失败");
            }
        }

        if (bIsSuccess == 1) {
//            return Result.success("msg","发布成功");
        }
//        return Result.error("msg","发布失败");
    }

    private void removeImages (Map<String,String> objectNames) {
//        ArrayList<String> alreadyRemove = new ArrayList<>();
//        objectNames.forEach(item -> {
//            try {
//                minioUtils.deleteFile(item);
//                alreadyRemove.add(item);
//            } catch (Exception ex) {
//                //TODO 可以使用消息中间件重新删除
//            }
//        });
    }
}
