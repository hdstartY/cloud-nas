package org.hdstart.cloud.controller;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.entity.Blog;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.BlogService;
import org.hdstart.cloud.vo.RecoverBlogVo;
import org.hdstart.cloud.vo.ShowBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HostnameVerifier;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("/blog/")
public class BlogController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BlogService blogService;

    @PostMapping("publishBlog")
    public Result<Map<String,String>> publicBlog (@RequestParam("memberId") Integer memberId,
                                                  @RequestParam("isPublic") Integer isPublic,
                                                  @RequestParam(value = "textContent",required = false) String textContent,
                                                  @RequestParam(value = "images",required = false) List<MultipartFile> images) {

        if (memberId == null) {
            return Result.error("msg","未知错误，登录状态可能异常");
        }
        if (isPublic == null) {
            return Result.error("msg","博客状态设置异常");
        }
        if (textContent.isEmpty() && images == null) {
            return Result.error("msg","不能发表空内容");
        }
        Result<Map<String,String>> result = blogService.publishBlog(new BlogFile(memberId,isPublic,textContent,images));
        return result;
    }

    @GetMapping("getBlogsByMemberId")
    public Result<List<ShowBlogVo>> getBlogsByMemberId (@RequestParam("memberId") Integer memberId,
                                                        @RequestParam("currentPage") Integer currentPage,
                                                        @RequestParam("pageSize") Integer pageSize) {
        List<ShowBlogVo> blogs = blogService.getBlogsByMemberId(memberId,currentPage,pageSize);
        return Result.success(blogs);
    }

    @GetMapping("getBlogById")
    public Result<ShowBlogVo> getBlogById (@RequestParam("blogId") Integer blogId) {
        ShowBlogVo blogs = blogService.getBlogById(blogId);
        return Result.success(blogs);
    }

    @GetMapping("list")
    public Result<List<ShowBlogVo>> listBlogs (@RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage,
                                               @RequestParam(value = "pageSize",required = false,defaultValue = "40") Integer pageSize,
                                               @RequestParam(value = "orderType") String orderType) throws ExecutionException, InterruptedException {

        List<ShowBlogVo> showBlogVoList = blogService.listShowBlogs(currentPage,pageSize,orderType);
        return Result.success(showBlogVoList);
    }

    @GetMapping("removeById")
    public Result<String> removeById (@RequestParam("blogId") Integer blogId) {
        LocalDateTime deletedTime = LocalDateTime.now();
        blogService.removeByIdWithTime(blogId,deletedTime);
        return Result.success("删除成功");
    }

    @GetMapping("listRecoverBlogs")
    public Result<List<RecoverBlogVo>> listRecoverBlogs (@RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage,
                                                         @RequestParam(value = "pageSize",required = false,defaultValue = "15") Integer pageSize,
                                                         @RequestParam("memberId") Integer memberId,
                                                         @RequestParam(value = "timeOrderType",required = false,defaultValue = "desc") String timeOrderType,
                                                         @RequestParam(value = "interval",required = false,defaultValue = "7") Integer interval) {
        List<RecoverBlogVo> recoverBlogVos = blogService.listRecoverBlogs(currentPage,pageSize,memberId,timeOrderType,interval);
        return Result.success(recoverBlogVos);
    }

    @PostMapping("resumeByIds")
    public Result resumeByIds (@RequestBody List<Integer> blogIds) {

        Integer isSuccess = blogService.resumeByIds(blogIds);
        return Result.success(isSuccess);
    }

    @GetMapping("removeByIdP")
    public Result removeByIdP (@RequestParam("blogId") Integer blogId) {
        Boolean num = blogService.removeByIdP(blogId);
        return Result.success(num);
    }

    @PostMapping("updateBlog")
    public Result updateBlog (@RequestParam(value = "textContent",required = false,defaultValue = "") String textContent,
                              @RequestParam(value = "blogId") Integer blogId,
                              @RequestParam(value = "removeImgUrls",required = false,defaultValue = "") List<String> removeImgUrls,
                              @RequestParam(value = "images",required = false) List<MultipartFile> images) {



        if (textContent.isEmpty() && images == null && removeImgUrls.isEmpty()) {
            return Result.build(400,"修改内容为空！",null);
        }

        blogService.updateBlog(blogId,textContent,removeImgUrls,images);
        return Result.success("更新成功");
    }

    @GetMapping("changeBlogStatus")
    public Result changeBlogStatus (@RequestParam(value = "blogId") Integer blogId,
                                    @RequestParam(value = "isPublic") Integer isPublic) {

        Blog blog = new Blog();
        blog.setId(blogId);
        blog.setIsPublic(isPublic);
        blogService.updateById(blog);
        return Result.success("更新状态成功");
    }

    private static final String HOT_BLOG_CACHE_KEY = "hot:blog:page";
    @GetMapping("getBlogVosByCache")
    public Result<List<ShowBlogVo>> getBlogsByCache (@RequestParam(value = "currentPage") Integer currentPage) {

        long startTime = System.currentTimeMillis();

        String key = HOT_BLOG_CACHE_KEY + currentPage;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null && !json.isEmpty()) {
            List<ShowBlogVo> vos = JSON.parseArray(json, ShowBlogVo.class);
            long endTime = System.currentTimeMillis();
            log.info("缓存费时：" + (endTime - startTime));
            return Result.success(vos);
        }

        // 缓存没命中，尝试加锁防止击穿
        ReentrantLock lock = new ReentrantLock();
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    // 再次检查缓存，防止其他线程已写入
                    json = stringRedisTemplate.opsForValue().get(key);
                    if (StringUtils.hasText(json)) {
                        return Result.success(JSON.parseArray(json, ShowBlogVo.class));
                    }
                    // 查询数据库
                    List<ShowBlogVo> list = blogService.listShowBlogsCache(currentPage,20);
                    if (list != null && !list.isEmpty()) {
                        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(list), 30, TimeUnit.MINUTES);
                    }
                    long endTime = System.currentTimeMillis();
                    log.info("查询数据库费时：" + (endTime - startTime));
                    return Result.success(list);
                } finally {
                    lock.unlock();
                }
            } else {
                // 获取锁失败，短暂等待或返回空
                Thread.sleep(100);
                json = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(json)) {
                    return Result.success(JSON.parseArray(json, ShowBlogVo.class));
                }
                return Result.success(Collections.emptyList());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.success(Collections.emptyList());
        }
    }

    @GetMapping("storeLike")
    public Result storeLike (@RequestParam("memberId") Integer memberId,
                             @RequestParam("blogId") Integer blogId) {

        Boolean isSuccess = blogService.storeLike(blogId, memberId);
        if (isSuccess) {
            return Result.success("点赞成功");
        } else {
            return Result.success("已取消");
        }
    }
}
