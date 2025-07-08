package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.Images;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hdstart.cloud.vo.BlogImgUrlVo;

import java.util.ArrayList;
import java.util.List;

/**
* @author 32600
* @description 针对表【images】的数据库操作Mapper
* @createDate 2025-05-28 19:37:42
* @Entity org.hdstart.cloud.entity.Images
*/
@Mapper
public interface ImagesMapper extends BaseMapper<Images> {

    List<String> selectListUrls(@Param("blogId") Integer blogId);

    List<BlogImgUrlVo> listUrlBatchBlogIds(@Param("voBlogIds") List<Integer> voBlogIds);

    Integer deleteBatchBlogIds(@Param("blogIds") ArrayList<Integer> blogIds);

    void deleteBatchUrls(@Param("removeImgUrls") List<String> removeImgUrls);
}




