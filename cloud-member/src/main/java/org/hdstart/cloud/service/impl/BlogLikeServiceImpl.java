package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.entity.BlogLike;
import org.hdstart.cloud.service.BlogLikeService;
import org.hdstart.cloud.mapper.BlogLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 32600
* @description 针对表【blog_like】的数据库操作Service实现
* @createDate 2025-06-12 19:30:58
*/
@Service
public class BlogLikeServiceImpl extends ServiceImpl<BlogLikeMapper, BlogLike>
    implements BlogLikeService{

    @Autowired
    private BlogLikeMapper blogLikeMapper;

    @Override
    public void removeBatch(List<BlogLike> like_task_cancel) {

    }
}




