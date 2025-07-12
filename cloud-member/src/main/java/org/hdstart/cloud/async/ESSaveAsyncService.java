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

    @Async
    public void saveBlogInfo (String blogId, String memberId, String textContent, List<String> imgUrls,Integer isPublic) {
        if (isPublic == 0 || textContent == null || textContent.isEmpty()) {
            log.warn("博客非公开状态，或文字内容为空，停止同步到ES...");
            return;
        }
        log.info("同步博客内容到ES...");
        ESBlogInfo esBlogInfo = new ESBlogInfo();
        Member member = memberMapper.selectById(Integer.valueOf(memberId));
        Blog blog = blogMapper.selectById(Integer.valueOf(blogId));
        esBlogInfo.setId(blogId);
        esBlogInfo.setMemberId(memberId);
        esBlogInfo.setAvatar(member.getAvatar());
        esBlogInfo.setNickName(member.getNickName());
        esBlogInfo.setTextContent(blog.getTextContent());
        esBlogInfo.setCreateTime(blog.getCreateTime().toString());
        esBlogInfo.setImages(imgUrls);

        try {
            elasticsearchClient.index(i -> i
                    .index("bloginfo")
                    .id(blogId)
                    .document(esBlogInfo)
            );
            log.info("保存博客到ES成功...");
        } catch (IOException e) {
            log.error("保存博客到ES失败...");
        }
    }
}
