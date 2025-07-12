package org.hdstart.cloud.elasticsearch.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.elasticsearch.entity.ESBlogInfo;
import org.hdstart.cloud.elasticsearch.entity.ESMemberInfo;
import org.hdstart.cloud.elasticsearch.service.IndexService;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ESIndexInitializer {

    private final IndexService indexService;

    public ESIndexInitializer(IndexService indexService) {
        this.indexService = indexService;
    }

    @PostConstruct
    public void init() {
        log.info("创建索引...");
        indexService.safeCreateIndex("memberinfo", ESMemberInfo.class);
        indexService.safeCreateIndex("bloginfo", ESBlogInfo.class);
    }
}
