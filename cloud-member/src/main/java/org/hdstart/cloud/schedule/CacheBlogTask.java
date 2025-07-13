package org.hdstart.cloud.schedule;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.elasticsearch.entity.ESBlogInfo;
import org.hdstart.cloud.entity.Blog;
import org.hdstart.cloud.entity.BlogLike;
import org.hdstart.cloud.mapper.PointMessageMapper;
import org.hdstart.cloud.service.BlogLikeService;
import org.hdstart.cloud.service.BlogService;
import org.hdstart.cloud.service.PointMessageService;
import org.hdstart.cloud.service.impl.BlogServiceImpl;
import org.hdstart.cloud.vo.ShowBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private PointMessageService pointMessageService;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private static final String HOT_BLOG_CACHE_KEY = "hot:blog:page";

    @Scheduled(fixedRate = 30 * 60 * 1000) // 每30分钟执行一次
    public void refreshHotBlogCache() {
        log.warn("执行数据预热");
        int pages = 5;
        int pageSize = 20;

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
        log.warn("开始持久化存储点赞数据.......");

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

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanChatHistory() {
        log.warn("删除七天前的聊天记录...");
        log.warn("删除文本内容...");
        Boolean text = pointMessageService.cleanChatHistory();
        log.warn("删除图片内容...");
        Boolean img = pointMessageService.cleanChatImg();
        if (text) {
            log.info("聊天记录清理完成...");
        } else {
            log.error("聊天记录清理失败...");
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void blogStoreES() {
        log.warn("同步公开博客到ES...");
        try {
            stringRedisTemplate.opsForValue().set("es_store_lock", "lock", 30, TimeUnit.SECONDS);
            Set<String> blogIds = stringRedisTemplate.opsForSet().members("blog_store_es");
            if (blogIds == null || blogIds.isEmpty()) {
                log.warn("需要同步的博客内容为空，停止同步...");
                return;
            }
            List<Integer> integerList = blogIds.stream().map(Integer::parseInt).collect(Collectors.toList());
            List<ESBlogInfo> esBlogInfos = blogService.getESBlogList(integerList);
            if (esBlogInfos == null || esBlogInfos.isEmpty()) {
                log.warn("需要同步的博客内容为空，停止同步...");
                return;
            }
            esBlogInfos.forEach(item -> {
                try {
                    elasticsearchClient.index(i -> i
                            .index("bloginfo")
                            .id(item.getId())
                            .document(item)
                    );
                } catch (IOException e) {
                    log.error("有内容同步失败：" + item);
                }
            });

            log.info("博客同步到ES完成...");
        } finally {
            stringRedisTemplate.delete("blog_store_es");
            stringRedisTemplate.delete("es_store_lock");
        }
    }
}
