package org.hdstart.cloud.service;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.BlogLike;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 32600
* @description 针对表【blog_like】的数据库操作Service
* @createDate 2025-06-12 19:30:58
*/
public interface BlogLikeService extends IService<BlogLike> {

    void removeBatch(@Param("like_task_cancel") List<BlogLike> like_task_cancel);
}
