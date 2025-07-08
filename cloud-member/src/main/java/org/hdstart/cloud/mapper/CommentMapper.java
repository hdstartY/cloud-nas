package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hdstart.cloud.vo.BlogCommentCountVo;
import org.hdstart.cloud.vo.ShowCommentVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author 32600
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2025-05-27 20:01:30
* @Entity org.hdstart.cloud.entity.Comment
*/
public interface CommentMapper extends BaseMapper<Comment> {

    List<ShowCommentVo> listCWithMBatchBlogIds(@Param("currentPage")Integer currentPage,@Param("pageSize") Integer pageSize,@Param("blogIds") List<Integer> blogIds);

    List<BlogCommentCountVo> listCommentCountByBlogIds(@Param("voBlogIds") List<Integer> voBlogIds);

    List<ShowCommentVo> listCWithMBatchBlogIdsF(@Param("voBlogIds") List<Integer> voBlogIds);

    Integer deleteBatchBlogIds(@Param("blogIds") ArrayList<Integer> blogIds);
}




