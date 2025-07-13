package org.hdstart.cloud.async;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.elasticsearch.entity.ESBlogInfo;
import org.hdstart.cloud.entity.Blog;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.mapper.BlogMapper;
import org.hdstart.cloud.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ESSaveAsyncService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private BlogMapper blogMapper;


}
