package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hdstart.cloud.vo.CheckContentVo;
import org.hdstart.cloud.vo.RecoverBlogVo;
import org.hdstart.cloud.vo.ShowBlogVo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* @author 32600
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2025-05-27 22:21:21
* @Entity org.hdstart.cloud.entity.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {


    List<ShowBlogVo> selectBlogVoByMemberId(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("memberId") Integer memberId);

    List<ShowBlogVo> listBlogWithMember(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("orderType") String orderType, @Param("now") LocalDateTime now, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    List<ShowBlogVo> listByMemberIds(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("followedIds") List<Integer> followedIds, @Param("orderType") String orderType);

    List<RecoverBlogVo> listRecoverBlogs(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("memberId") Integer memberId, @Param("timeOrderType") String timeOrderType, @Param("frontTime") LocalDateTime frontTime, @Param("now") LocalDateTime now);


    Integer removePByIds(@Param("blogIds") ArrayList<Integer> blogIds);

    Integer resumeByIds(@Param("blogIds") List<Integer> blogIds);

    void removeByIdWithTime(@Param("blogId") Integer blogId, @Param("deletedTime") LocalDateTime deletedTime);

    List<CheckContentVo> listBlog(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize);

    List<ShowBlogVo> listBlogCache(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("now") LocalDateTime now, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

}




