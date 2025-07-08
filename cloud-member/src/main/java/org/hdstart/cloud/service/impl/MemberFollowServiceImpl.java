package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.entity.MemberFollow;
import org.hdstart.cloud.entity.OtherMemberInfo;
import org.hdstart.cloud.mapper.*;
import org.hdstart.cloud.service.MemberFollowService;
import org.hdstart.cloud.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author 32600
* @description 针对表【member_follow】的数据库操作Service实现
* @createDate 2025-06-01 12:29:00
*/
@Service
@Slf4j
public class MemberFollowServiceImpl extends ServiceImpl<MemberFollowMapper, MemberFollow>
    implements MemberFollowService{

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private ImagesMapper imagesMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MemberFollowMapper memberFollowMapper;

    @Autowired
    private OtherMemberInfoMapper otherMemberInfoMapper;

    @Override
    public List<ShowBlogVo> listFollowedBlogs(Integer currentPage, Integer pageSize, List<Integer> followedIds, String orderType) {
        long l = System.currentTimeMillis();
        List<ShowBlogVo> vos = blogMapper.listByMemberIds((currentPage - 1) * pageSize,pageSize,followedIds,orderType);
        if (vos == null || vos.isEmpty()) {
            return null;
        }
        List<Integer> blogIds = vos.stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        //获得所有图片信息
        List<BlogImgUrlVo> blogImgUrlVos = imagesMapper.listUrlBatchBlogIds(blogIds);
        HashMap<Integer, List<String>> blogMapUrl = new HashMap<>();
        blogImgUrlVos.stream().collect(Collectors.groupingBy(BlogImgUrlVo::getBlogId)).entrySet().stream().forEach(entry -> {
            Integer blogId = entry.getKey();
            List<String> imgUrls = entry.getValue().stream().map(item -> {
                return item.getImgUrl();
            }).collect(Collectors.toList());
            blogMapUrl.put(blogId,imgUrls);
        });
        //评论
        List<BlogCommentCountVo> blogCommentCountVos = commentMapper.listCommentCountByBlogIds(blogIds);
        Map<Integer, Long> blogMapCount = blogCommentCountVos.stream().collect(Collectors.toMap(item -> item.getBlogId(), item -> item.getCount()));

        List<ShowCommentVo> showCommentVos = commentMapper.listCWithMBatchBlogIdsF(blogIds);
        System.out.println(showCommentVos);
        Map<Integer, List<ShowCommentVo>> commentGroup = showCommentVos.stream().collect(Collectors.groupingBy(ShowCommentVo::getBlogId));
        HashMap<Integer, List<ShowCommentVo>> blogIdMapComment = new HashMap<>();
        commentGroup.entrySet().stream().forEach(entry -> {
            Integer blogId = entry.getKey();
            List<ShowCommentVo> items = entry.getValue();
            blogIdMapComment.put(blogId, items);
        });

        //组装
        List<ShowBlogVo> showBlogVos = vos.stream().map(item -> {
            List<String> urls = blogMapUrl.get(item.getId());
            item.setImages(urls);
            Long count = blogMapCount.get(item.getId());
            if (count != null) {
                item.setCommentNum(count);
            }
            List<ShowCommentVo> commentVos = blogIdMapComment.get(item.getId());
            if (commentVos != null) {
                item.setComments(commentVos);
            }
            return item;
        }).collect(Collectors.toList());

        long l1 = System.currentTimeMillis();
        log.info("关注列表查询：" + (l1- l));
        return showBlogVos;
    }

    @Override
    public FanCountsVo getFansCounts(Integer memberId) {
        Long followCounts = memberFollowMapper.selectCount(new QueryWrapper<MemberFollow>().eq("follower_id", memberId));
        Long fanCounts = memberFollowMapper.selectCount(new QueryWrapper<MemberFollow>().eq("followed_id", memberId));
        OtherMemberInfo other = otherMemberInfoMapper.selectOne(new QueryWrapper<OtherMemberInfo>().eq("member_id", memberId));
        FanCountsVo fanCountsVo = new FanCountsVo();
        fanCountsVo.setFollowCounts(followCounts);
        fanCountsVo.setFanCounts(fanCounts);
        if (other != null) {
            fanCountsVo.setBackImg(other.getBackImg());
            fanCountsVo.setSignature(other.getSignature());
            fanCountsVo.setOther(other.getOther());
        }
        return fanCountsVo;
    }
}




