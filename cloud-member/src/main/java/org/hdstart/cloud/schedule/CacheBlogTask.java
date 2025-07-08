package org.hdstart.cloud.schedule;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.entity.Blog;
import org.hdstart.cloud.entity.BlogLike;
import org.hdstart.cloud.service.BlogLikeService;
import org.hdstart.cloud.service.BlogService;
import org.hdstart.cloud.service.impl.BlogServiceImpl;
import org.hdstart.cloud.vo.ShowBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CacheBlogTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogLikeService blogLikeService;

    private static final String HOT_BLOG_CACHE_KEY = "hot:blog:page";

    @Scheduled(fixedRate = 30 * 60 * 1000) // 每30分钟执行一次
    public void refreshHotBlogCache() {
        log.error("执行数据预热");
        int pages = 5;
        int pageSize = 10;

        for (int currentPage = 1; currentPage <= pages; currentPage++) {
            List<ShowBlogVo> showBlogVos = blogService.listShowBlogsCache(currentPage, pageSize);
            if (showBlogVos == null || showBlogVos.isEmpty()) {
                break;
            }
            String key = HOT_BLOG_CACHE_KEY + currentPage;
            String json = JSON.toJSONString(showBlogVos);
            stringRedisTemplate.opsForValue().set(key,json,30, TimeUnit.MINUTES);
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void saveBLogLike () {
        log.info("开始持久化存储点赞数据.......");

        //点赞存储
        List<BlogLike> like_task_store = blogService.getBlogLikes("like_task_add");

        if (like_task_store == null || like_task_store.isEmpty()) {
            log.info("点赞列表为空，停止同步");
        } else {
            blogLikeService.saveBatch(like_task_store);
            blogService.cleanSaved("like_task_add");
            Map<Integer, Long> blogIdMapLikeNum = like_task_store.stream().collect(Collectors.groupingBy(BlogLike::getBlogId, Collectors.counting()));
            blogIdMapLikeNum.entrySet().stream().forEach(item -> {
                Blog blog = new Blog();
                blog.setId(item.getKey());
                blog.setLikeNum(item.getValue().intValue());
                blogService.updateById(blog);
            });
            log.info("点赞列表同步完成");
        }
    }
}
