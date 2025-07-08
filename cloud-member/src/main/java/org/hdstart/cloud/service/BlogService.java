package org.hdstart.cloud.service;

import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.entity.BlogLike;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.vo.CheckContentVo;
import org.hdstart.cloud.vo.RecoverBlogVo;
import org.hdstart.cloud.vo.ShowBlogVo;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
* @author 32600
* @description 针对表【blog】的数据库操作Service
* @createDate 2025-05-27 22:21:21
*/
public interface BlogService extends IService<Blog> {

    Result<Map<String,String>> publishBlog(BlogFile blogFile);

    List<ShowBlogVo> getBlogsByMemberId(Integer memberId,Integer currentPage,Integer pageSize);

    ShowBlogVo getBlogById(Integer blogId);

    List<ShowBlogVo> listShowBlogs(Integer currentPage, Integer pageSize,String orderType) throws ExecutionException, InterruptedException;

    List<RecoverBlogVo> listRecoverBlogs(Integer currentPage, Integer pageSize, Integer memberId, String timeOrderType, Integer interval);

    Integer resumeByIds(List<Integer> blogIds);

    Boolean removeByIdP(Integer blogId);

    void updateBlog(Integer blogId, String textContent, List<String> removeImgUrls, List<MultipartFile> images);

    void removeByIdWithTime(Integer blogId, LocalDateTime deletedTime);

    List<CheckContentVo> getCheckContent(Integer currentPage, Integer pageSize);

    void passContent(Integer blogId);

    List<ShowBlogVo> listShowBlogsCache(Integer currentPage, Integer pageSize);

    Boolean storeLike(Integer blogId, Integer memberId);

    List<BlogLike> getBlogLikes (String key);

    void cleanSaved (String key);
}
