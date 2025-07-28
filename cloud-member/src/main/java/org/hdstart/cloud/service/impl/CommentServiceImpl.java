package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.entity.Comment;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.mapper.MemberMapper;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.CommentService;
import org.hdstart.cloud.mapper.CommentMapper;
import org.hdstart.cloud.vo.ShowCommentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 32600
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2025-05-27 20:01:30
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public List<ShowCommentVo> listByBlogId(Integer id, Integer currentPage, Integer pageSize) {

//        Page<Comment> commentPage = new Page<>(currentPage, pageSize);
//        //查询评论基础信息
//        List<Comment> comments = commentMapper.selectList(commentPage, new QueryWrapper<Comment>().eq("blog_id", id));
//
//
//        List<ShowCommentVo> showCommentVos = comments.stream().map(item -> {
//            Member member = memberMapper.selectById(item.getMemberId());
//            ShowCommentVo showCommentVo = new ShowCommentVo();
//            BeanUtils.copyProperties(item, showCommentVo);
//            showCommentVo.setCommentNickName(member.getNickName());
//            showCommentVo.setAvatar(member.getAvatar());
//            return showCommentVo;
//        }).collect(Collectors.toList());
//        return showCommentVos;
        List<ShowCommentVo> showCommentVos = commentMapper.listCWithMBatchBlogIds((currentPage - 1) * pageSize,pageSize,Arrays.asList(id));
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Page<Comment> commentPage = new Page<>(currentPage, pageSize);
//        //查询评论基础信息
//        List<Comment> comments = commentMapper.selectList(commentPage, new QueryWrapper<Comment>().eq("blog_id", id));
//        //收集发布者信息
//        Set<Integer> memberIds = comments.stream().map(item -> item.getMemberId()).collect(Collectors.toSet());
//        List<ShowCommentVo> showCommentVos = new ArrayList<>();
//        if (memberIds.size() > 0) {
//            List<Member> members = memberMapper.selectBatchIds(memberIds);
//            Map<Integer, Member> memberMap = members.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
//            //组装
//            showCommentVos = comments.stream().map(item -> {
//                ShowCommentVo showCommentVo = new ShowCommentVo();
//                BeanUtils.copyProperties(item, showCommentVo);
//                Member member = memberMap.get(item.getMemberId());
//                showCommentVo.setCommentNickName(member.getNickName());
//                showCommentVo.setAvatar(member.getAvatar());
//                return showCommentVo;
//            }).collect(Collectors.toList());
//        } else {
//            //组装
//            showCommentVos = comments.stream().map(item -> {
//                ShowCommentVo showCommentVo = new ShowCommentVo();
//                BeanUtils.copyProperties(item, showCommentVo);
//                return showCommentVo;
//            }).collect(Collectors.toList());
//        }
        return showCommentVos;
    }
}




