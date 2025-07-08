package org.hdstart.cloud.service;

import org.hdstart.cloud.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.vo.ShowCommentVo;

import java.util.List;

/**
* @author 32600
* @description 针对表【comment】的数据库操作Service
* @createDate 2025-05-27 20:01:30
*/
public interface CommentService extends IService<Comment> {

    List<ShowCommentVo> listByBlogId(Integer id, Integer currentPage, Integer pageSize);
}
