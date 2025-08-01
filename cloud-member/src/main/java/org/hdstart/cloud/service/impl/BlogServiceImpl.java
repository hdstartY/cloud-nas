package org.hdstart.cloud.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.hdstart.cloud.async.BlogAsyncService;
import org.hdstart.cloud.async.ESSaveAsyncService;
import org.hdstart.cloud.async.VideoThumbnailService;
import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.elasticsearch.entity.ESBlogInfo;
import org.hdstart.cloud.entity.*;
import org.hdstart.cloud.mapper.*;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.BlogService;
import org.hdstart.cloud.service.ImagesService;
import org.hdstart.cloud.to.ImgTo;
import org.hdstart.cloud.utils.minio.fileType.FileType;
import org.hdstart.cloud.utils.minio.utils.MinioUtils;
import org.hdstart.cloud.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 32600
 * @description 针对表【blog】的数据库操作Service实现
 * @createDate 2025-05-27 22:21:21
 */
@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ImagesMapper imagesMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BlogLikeMapper blogLikeMapper;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private ESSaveAsyncService esSaveAsyncService;

    @Autowired
    private BlogAsyncService blogAsyncService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    @Autowired
    private VideoThumbnailService videoThumbnailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Map<String, String>> publishBlog(BlogFile blogFile) {

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
            List<Images> objectNames = new ArrayList<>();
            for (MultipartFile file : blogFile.getImages()) {
                Images images = new Images();
                try {
                    boolean isVideo = videoThumbnailService.isAllowedVideoFormat(file.getContentType(), file.getOriginalFilename());
                    if (isVideo) {
                        try {
                            byte[] jpgs = videoThumbnailService.generateThumbnail(file, 1, "jpeg");
                            images.setPreUrl(minioUtils.uploadVideoPre(jpgs, file.getOriginalFilename(), FileType.VIDEO_PRE));
                            images.setOriUrl(minioUtils.uploadFile(file, FileType.VIDEO));
                            images.setIsVideo(1);
                        } catch (Exception e) {
                            log.error("视频生成缩略图失败！");
                        }
                    } else {
                        BufferedImage bufferedImage = Thumbnails.of(file.getInputStream()).size(120, 120).asBufferedImage();
                        images.setPreUrl(minioUtils.uploadPreFile(bufferedImage, file.getOriginalFilename(), FileType.PRE_IMG));
                        images.setOriUrl(minioUtils.uploadFile(file, FileType.BLOG));
                        images.setIsVideo(0);
                    }
                } catch (Exception e) {
                    //TODO 上传失败策略
//                    removeImages(objectNames);
                    System.out.println(e.fillInStackTrace());
                    throw new RuntimeException("发布失败！");
                }
                objectNames.add(images);
            }

            //保存图片数据
            ArrayList<Images> storeImages = new ArrayList<>();
            objectNames.stream().forEach(item -> {
                Images images = new Images();
                images.setBlogId(blog.getId());
                String previewUrl = minioUtils.getPreviewUrl(item.getPreUrl());
                String oriviewUrl = minioUtils.getPreviewUrl(item.getOriUrl());
                images.setPreUrl(previewUrl);
                images.setOriUrl(oriviewUrl);
                images.setIsVideo(item.getIsVideo());
                storeImages.add(images);
            });

            boolean iIsSuccess = imagesService.saveBatch(storeImages);
            if (!iIsSuccess) {
                throw new RuntimeException("发布失败");
            }
        }

        if (bIsSuccess == 1) {
            return Result.success("msg","发布成功");
        }
        return Result.error("msg","发布失败");
    }

    @Override
    public List<ShowBlogVo> getBlogsByMemberId(Integer memberId,Integer currentPage,Integer pageSize) {
        long l = System.currentTimeMillis();
        //获取所有基础博客
        List<ShowBlogVo> vos = blogMapper.selectBlogVoByMemberId((currentPage - 1) * pageSize,pageSize,memberId);
        if (vos == null || vos.isEmpty()) {
            return null;
        }
        List<Integer> blogIds = vos.stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        //获得所有图片信息
        List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(blogIds);
        HashMap<Integer, List<ImgTo>> blogMapUrl = new HashMap<>();
        blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId)).entrySet().stream().forEach(entry -> {
            Integer blogId = entry.getKey();
            List<ImgTo> imgUrls = entry.getValue().stream().map(item -> {
                ImgTo imgTo = new ImgTo();
                imgTo.setPreUrl(item.getPreUrl());
                imgTo.setOriUrl(item.getOriUrl());
                imgTo.setIsVideo(item.getIsVideo());
                return imgTo;
            }).collect(Collectors.toList());
            blogMapUrl.put(blogId,imgUrls);
        });
        //评论
        List<BlogCommentCountVo> blogCommentCountVos = commentMapper.listCommentCountByBlogIds(blogIds);
        Map<Integer, Long> blogMapCount = blogCommentCountVos.stream().collect(Collectors.toMap(item -> item.getBlogId(), item -> item.getCount()));

//        List<ShowCommentVo> showCommentVos = commentMapper.listCWithMBatchBlogIdsF(blogIds);
//        Map<Integer, List<ShowCommentVo>> commentGroup = showCommentVos.stream().collect(Collectors.groupingBy(ShowCommentVo::getBlogId));
//        HashMap<Integer, List<ShowCommentVo>> blogIdMapComment = new HashMap<>();
//        commentGroup.entrySet().stream().forEach(entry -> {
//            Integer blogId = entry.getKey();
//            List<ShowCommentVo> items = entry.getValue();
//            blogIdMapComment.put(blogId, items);
//        });

        //组装
        List<ShowBlogVo> showBlogVos = vos.stream().map(item -> {
            List<ImgTo> urls = blogMapUrl.get(item.getId());
            item.setImages(urls);
            Long count = blogMapCount.get(item.getId());
            if (count != null) {
                item.setCommentNum(count);
            }
//            List<ShowCommentVo> commentVos = blogIdMapComment.get(item.getId());
//            if (commentVos != null) {
//                item.setComments(commentVos);
//            }
            return item;
        }).collect(Collectors.toList());

        long l1 = System.currentTimeMillis();
        log.info("获取用户博客花费：" + (l1- l));
        return showBlogVos;
    }

    @Override
    public ShowBlogVo getBlogById(Integer blogId) {

        ShowBlogVo showBlogVo = new ShowBlogVo();
        //获取博客基本信息
        CompletableFuture<Integer> blogComplete = CompletableFuture.supplyAsync(() -> {
            Blog blog = blogMapper.selectById(blogId);
            BeanUtils.copyProperties(blog, showBlogVo);
            return blog.getMemberId();
        }, executor);
        //作者信息
        CompletableFuture<Void> memberComplete = blogComplete.thenAcceptAsync((memberId) -> {
            Member member = memberMapper.selectById(memberId);
            showBlogVo.setNickName(member.getNickName());
            showBlogVo.setAvatar(member.getAvatar());
        },executor);
        //图片信息
        CompletableFuture<Void> imgComplete = CompletableFuture.runAsync(() -> {
            List<ImgTo> urls = imagesMapper.selectListUrls(blogId);
            showBlogVo.setImages(urls);
        }, executor);

        //评论数量
        CompletableFuture<Void> commentCountComplete = CompletableFuture.runAsync(() -> {
            Long commentNum = commentMapper.selectCount(new QueryWrapper<Comment>().eq("blog_id", blogId));
            showBlogVo.setCommentNum(commentNum);
        }, executor);


        CompletableFuture.allOf(blogComplete,memberComplete,imgComplete,commentCountComplete).join();
        return showBlogVo;
    }

    @Override
    public List<ShowBlogVo> listShowBlogs(Integer currentPage, Integer pageSize,String orderType) {
        long startTime = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(30);

        //1.查询基础博客
        List<ShowBlogVo> vos = blogMapper.listBlogWithMember((currentPage - 1) * pageSize,pageSize,orderType,now,sevenDaysAgo);
        if (vos == null || vos.isEmpty()) {
            return null;
        }
        List<Integer> voBlogIds = vos.stream().map(item -> item.getId()).collect(Collectors.toList());
        //2.批量查询所有图片url
        List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(voBlogIds);
        Map<Integer, List<BlogImgUrlVo>> group = blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId));
        HashMap<Integer, List<ImgTo>> mapUrl = new HashMap<>();
        group.entrySet().stream().forEach(entry -> {
            Integer blogId = entry.getKey();
            List<BlogImgUrlVo> items = entry.getValue();
            List<ImgTo> urls = items.stream().map(item -> {
                ImgTo imgTo = new ImgTo();
                imgTo.setPreUrl(item.getPreUrl());
                imgTo.setOriUrl(item.getOriUrl());
                imgTo.setIsVideo(item.getIsVideo());
                return imgTo;
            }).collect(Collectors.toList());
            mapUrl.put(blogId, urls);
        });
        //3.查询评论
        List<BlogCommentCountVo> blogCommentCountVos = commentMapper.listCommentCountByBlogIds(voBlogIds);
        Map<Integer, Long> blogMapCount = blogCommentCountVos.stream().collect(Collectors.toMap(item -> item.getBlogId(), item -> item.getCount()));

        List<ShowCommentVo> showCommentVos = commentMapper.listCWithMBatchBlogIdsF(voBlogIds);
        Map<Integer, List<ShowCommentVo>> commentGroup = showCommentVos.stream().collect(Collectors.groupingBy(ShowCommentVo::getBlogId));
        HashMap<Integer, List<ShowCommentVo>> blogIdMapComment = new HashMap<>();
        commentGroup.entrySet().stream().forEach(entry -> {
            Integer blogId = entry.getKey();
            List<ShowCommentVo> items = entry.getValue();
            blogIdMapComment.put(blogId, items);
        });
        //组装
        List<ShowBlogVo> showBlogVos = vos.stream().map(item -> {
            List<ImgTo> urls = mapUrl.get(item.getId());
            List<ShowCommentVo> showCommentVoList = blogIdMapComment.get(item.getId());
            Long commentCounts = blogMapCount.get(item.getId());
            item.setImages(urls);
            if (showCommentVoList != null) {
                item.setComments(showCommentVoList);
            }
            if (commentCounts != null) {
                item.setCommentNum(commentCounts);
            }
            return item;
        }).collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("普通方法获取主页博客花费：" + (endTime - startTime));
        return showBlogVos;
    }

    @Override
    public List<RecoverBlogVo> listRecoverBlogs(Integer currentPage, Integer pageSize, Integer memberId,String timeOrderType, Integer interval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime frontTime = now.minusDays(interval);
        List<RecoverBlogVo> blogVos = blogMapper.listRecoverBlogs((currentPage - 1)*pageSize,pageSize,memberId,timeOrderType,frontTime,now);

        if (blogVos == null || blogVos.size() == 0) {
            return null;
        }

        List<Integer> blogIds = blogVos.stream().map(item -> item.getId()).collect(Collectors.toList());

        List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(blogIds);
        Map<Integer, List<BlogImgUrlVo>> imgMapBlogId = blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId));
        HashMap<Integer, List<String>> urlMapBlogId = new HashMap<>();
        imgMapBlogId.entrySet().stream().forEach(item -> {
            Integer blogId = item.getKey();
            List<String> urls = item.getValue().stream().map(img -> {
                String imgUrl = img.getPreUrl();
                return imgUrl;
            }).collect(Collectors.toList());
            urlMapBlogId.put(blogId, urls);
        });

        List<RecoverBlogVo> recoverBlogVos = blogVos.stream().map(item -> {
            List<String> urls = urlMapBlogId.get(item.getId());
            item.setImages(urls);
            return item;
        }).collect(Collectors.toList());

        return recoverBlogVos;
    }

    @Override
    public Integer resumeByIds(List<Integer> blogIds) {
        Integer num = blogMapper.resumeByIds(blogIds);
        return num;
    }

    @Transactional
    @Override
    public Boolean removeByIdP(Integer blogId) {
        ArrayList<Integer> blogIds = new ArrayList<>();
        blogIds.add(blogId);
        Integer bNum = blogMapper.removePByIds(blogIds);
        Integer cNum = commentMapper.deleteBatchBlogIds(blogIds);

        List<Images> images = imagesMapper.selectList(new QueryWrapper<Images>().eq("blog_id", blogId));
        if (images != null && images.size() > 0) {
            List<String> urls = images.stream().map(item -> {
                String imgUrl = item.getPreUrl();
                return imgUrl;
            }).collect(Collectors.toList());

            urls.stream().forEach(item -> {
                int count = 0, index = 0;
                for (int i = 0; i < item.length(); i++) {
                    char c = item.toCharArray()[i];
                    if (c == '/') {
                        count += 1;
                    }
                    if (count == 4) {
                        index = i;
                        break;
                    }
                }
                String objectName = item.substring(index + 1);
                minioUtils.deleteFile(objectName);
            });
        }

        Integer iNum = imagesMapper.deleteBatchBlogIds(blogIds);

        return true;
    }

    //TODO 注意更新该方法
    @Transactional
    @Override
    public void updateBlog(Integer blogId, String textContent, List<String> removeImgUrls, List<MultipartFile> images) {
        if (textContent != null && !textContent.isEmpty()) {
            Blog blog = new Blog();
            blog.setTextContent(textContent);
            blog.setId(blogId);
            blog.setIsPublic(2);
            blogMapper.updateById(blog);
        }

        if (removeImgUrls != null && !removeImgUrls.isEmpty()) {
            imagesMapper.deleteBatchUrls(removeImgUrls);
            removeImgUrls.stream().forEach(item -> {
                int count = 0, index = 0;
                for (int i = 0; i < item.length(); i++) {
                    char c = item.toCharArray()[i];
                    if (c == '/') {
                        count += 1;
                    }
                    if (count == 4) {
                        index = i;
                        break;
                    }
                }
                String objectName = item.substring(index + 1);
                minioUtils.deleteFile(objectName);
            });
        }

        List<String> objectNames = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            try {
                images.stream().forEach(item -> {
                    String objectName = minioUtils.uploadFile(item, FileType.BLOG);
                    objectNames.add(objectName);
                });
            } catch (Exception e) {
//                removeImages(objectNames);
                throw new RuntimeException(e);
            }
        }
        List<Images> imagesList = objectNames.stream().map(item -> {
            String previewUrl = minioUtils.getPreviewUrl(item);
            Images preStoreImg = new Images();
            preStoreImg.setPreUrl(previewUrl);
            preStoreImg.setBlogId(blogId);
            return preStoreImg;
        }).collect(Collectors.toList());
        boolean b = imagesService.saveBatch(imagesList);
    }

    @Override
    public void removeByIdWithTime(Integer blogId, LocalDateTime deletedTime) {

        blogMapper.removeByIdWithTime(blogId,deletedTime);
    }

    @Override
    public List<CheckContentVo> getCheckContent(Integer currentPage, Integer pageSize) {
        List<CheckContentVo> vos = blogMapper.listBlog((currentPage - 1) * pageSize,pageSize);
        List<Integer> blogIds = vos.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(blogIds);
        HashMap<Integer, List<String>> blogIdMapUrl = new HashMap<>();
        blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId)).entrySet().stream().forEach(item -> {
            Integer blogId = item.getKey();
            List<String> urls = item.getValue().stream().map(img -> {
                String imgUrl = img.getPreUrl();
                return imgUrl;
            }).collect(Collectors.toList());
            blogIdMapUrl.put(blogId,urls);
        });
        List<CheckContentVo> checkContentVos = vos.stream().map(item -> {
            List<String> urls = blogIdMapUrl.get(item.getId());
            item.setImages(urls);
            return item;
        }).collect(Collectors.toList());

        return checkContentVos;
    }

    @Override
    public Boolean passContent(Integer blogId) {
            Boolean es_store_lock = stringRedisTemplate.hasKey("es_store_lock");
            if (es_store_lock) {
                log.error("有任务执行中...稍后再试");
                return false;
            }
            Blog blog = new Blog();
            blog.setId(blogId);
            blog.setIsPublic(1);
            blogMapper.updateById(blog);
            stringRedisTemplate.opsForSet().add("blog_store_es",blogId.toString());
            return true;
    }

    @Override
    public List<ShowBlogVo> listShowBlogsCache(Integer currentPage, Integer pageSize) {
        long startTime = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(30);

        //1.查询基础博客
        List<ShowBlogVo> vos = blogMapper.listBlogCache((currentPage - 1) * pageSize,pageSize,now,sevenDaysAgo);
        if (vos == null || vos.isEmpty()) {
            return null;
        }
        List<Integer> voBlogIds = vos.stream().map(item -> item.getId()).collect(Collectors.toList());
        //2.批量查询所有图片url
        List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(voBlogIds);
        Map<Integer, List<BlogImgUrlVo>> group = blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId));
        HashMap<Integer, List<ImgTo>> mapUrl = new HashMap<>();
        group.entrySet().stream().forEach(entry -> {
            Integer blogId = entry.getKey();
            List<BlogImgUrlVo> items = entry.getValue();
            List<ImgTo> urls = items.stream().map(item -> {
                ImgTo imgTo = new ImgTo();
                imgTo.setPreUrl(item.getPreUrl());
                imgTo.setOriUrl(item.getOriUrl());
                imgTo.setIsVideo(item.getIsVideo());
                return imgTo;
            }).collect(Collectors.toList());
            mapUrl.put(blogId, urls);
        });
        //3.查询评论
        List<BlogCommentCountVo> blogCommentCountVos = commentMapper.listCommentCountByBlogIds(voBlogIds);
        Map<Integer, Long> blogMapCount = blogCommentCountVos.stream().collect(Collectors.toMap(item -> item.getBlogId(), item -> item.getCount()));

//        List<ShowCommentVo> showCommentVos = commentMapper.listCWithMBatchBlogIdsF(voBlogIds);
//        Map<Integer, List<ShowCommentVo>> commentGroup = showCommentVos.stream().collect(Collectors.groupingBy(ShowCommentVo::getBlogId));
//        HashMap<Integer, List<ShowCommentVo>> blogIdMapComment = new HashMap<>();
//        commentGroup.entrySet().stream().forEach(entry -> {
//            Integer blogId = entry.getKey();
//            List<ShowCommentVo> items = entry.getValue();
//            blogIdMapComment.put(blogId, items);
//        });
        //组装
        List<ShowBlogVo> showBlogVos = vos.stream().map(item -> {
            List<ImgTo> urls = mapUrl.get(item.getId());
//            List<ShowCommentVo> showCommentVoList = blogIdMapComment.get(item.getId());
            Long commentCounts = blogMapCount.get(item.getId());
            item.setImages(urls);
//            if (showCommentVoList != null) {
//                item.setComments(showCommentVoList);
//            }
            if (commentCounts != null) {
                item.setCommentNum(commentCounts);
            }
            return item;
        }).collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info("缓存方法从数据库获取主页博客花费" + (endTime - startTime));
        return showBlogVos;
    }


    @Override
    public Boolean storeLike(Integer blogId, Integer memberId) {
        long startTime = System.currentTimeMillis();

        String key = String.valueOf(blogId);
        String like = String.valueOf(memberId);

//        存储点赞
        Long isNew = stringRedisTemplate.opsForSet().add(key, like);
        stringRedisTemplate.expire(key,7,TimeUnit.DAYS);

        if (isNew == 1) {
            stringRedisTemplate.opsForSet().add("like_task_add",key + ":" + like);
            long endTime = System.currentTimeMillis();
            log.info("点赞花费：" + (endTime - startTime));
            return true;
        } else {
            stringRedisTemplate.opsForSet().remove(key, like);
            Long isDelete = stringRedisTemplate.opsForSet().remove("like_task_add", key + ":" + like);
            if (isDelete == 1) {
                blogLikeMapper.delete(new QueryWrapper<BlogLike>().eq("blog_id", key).eq("member_id", like));
            }
            long endTime = System.currentTimeMillis();
            log.info("取消花费：" + (endTime - startTime));
            return false;
        }
    }

    @Override
    public List<BlogLike> getBlogLikes (String key) {

        Set like_task = stringRedisTemplate.opsForSet().members(key);
        ArrayList<BlogLike> blogLikes = new ArrayList<>();

        if (like_task != null) {
            for (Object item : like_task) {
                String[] value = item.toString().split(":");
                Integer blogId = Integer.valueOf(value[0]);
                Integer memberId = Integer.valueOf(value[1]);
                BlogLike blogLike = new BlogLike();
                blogLike.setBlogId(blogId);
                blogLike.setMemberId(memberId);

                blogLikes.add(blogLike);
            }
        }

        return blogLikes;
    }

    @Override
    public void cleanSaved (String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Result<List<ESBlogInfo>> searchByES(String searchValue,Integer currentPage,Integer pageSize) {

        SearchResponse<ESBlogInfo> response = null;
        try {
             response = elasticsearchClient.search(s -> s
                            .index("bloginfo")
                            .from((currentPage - 1) * pageSize)
                            .size(pageSize)
                            .query(q -> q
                                    .bool(b -> b
                                            .should(sh1 -> sh1
                                                    .match(m -> m
                                                            .field("textContent")
                                                            .query(searchValue)
                                                    )
                                            ).should(sh2 -> sh2
                                                    .match(m -> m
                                                            .field("nickName")
                                                            .query(searchValue)
                                                    )
                                            )
                                    )
                            ),
                    ESBlogInfo.class
            );
        } catch (IOException e) {
            log.error("ES查询blog出错...");
            return Result.build(500,"查询出错",null);
        }
        List<ESBlogInfo> resultList = response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .toList();

        return Result.success(resultList);
    }

    @Override
    public List<ESBlogInfo> getESBlogList(List<Integer> integerList) {

        List<ESBlogInfo> esBlogInfos = new ArrayList<>();
        CompletableFuture<Void> getVos = CompletableFuture.runAsync(() -> {
            List<ESBlogInfo> vos = blogMapper.selectESBlogList(integerList);
            esBlogInfos.addAll(vos);
        }, executor);

        HashMap<Integer, List<ImgTo>> blogIdMapUrl = new HashMap<>();
        CompletableFuture<Void> getImgUrls = CompletableFuture.runAsync(() -> {
            List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(integerList);
            Map<Integer, List<BlogImgUrlVo>> group = blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId));
            group.entrySet().stream().forEach(entry -> {
                Integer blogId = entry.getKey();
                List<BlogImgUrlVo> items = entry.getValue();
                List<ImgTo> urls = items.stream().map(item -> {
                    ImgTo imgTo = new ImgTo();
                    imgTo.setPreUrl(item.getPreUrl());
                    imgTo.setOriUrl(item.getOriUrl());
                    imgTo.setIsVideo(item.getIsVideo());
                    return imgTo;
                }).collect(Collectors.toList());
                blogIdMapUrl.put(blogId, urls);
            });
        }, executor);

        CompletableFuture.allOf(getVos,getImgUrls).join();

        if (esBlogInfos.isEmpty()) return null;

        List<ESBlogInfo> ESBlogInfoList = esBlogInfos.stream().map(item -> {
            List<ImgTo> imgUrls = blogIdMapUrl.get(Integer.valueOf(item.getId()));
            if (imgUrls != null && !imgUrls.isEmpty()) {
                item.setImages(imgUrls);
            }
            return item;
        }).collect(Collectors.toList());

        return ESBlogInfoList;
    }

    @Override
    public Result<List<ESBlogInfo>> searchBlogByES(String searchValue, Integer currentPage, Integer pageSize) {
        SearchResponse<ESBlogInfo> response = null;
        try {
            response = elasticsearchClient.search(s -> s
                            .index("bloginfo")
                            .from((currentPage - 1) * pageSize)
                            .size(pageSize)
                            .query(q -> q
                                    .match(m -> m
                                            .field("textContent")
                                            .query(searchValue)
                                    )
                            ),
                    ESBlogInfo.class
            );
        } catch (IOException e) {
            log.error("ES查询blog出错...");
            return Result.build(500,"查询出错",null);
        }
        List<ESBlogInfo> resultList = response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .toList();

        return Result.success(resultList);
    }

    private void removeImages (List<Images> images) {
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




