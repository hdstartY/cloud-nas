package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.MsgImg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 32600
* @description 针对表【msg_img】的数据库操作Mapper
* @createDate 2025-07-10 16:29:50
* @Entity org.hdstart.cloud.entity.MsgImg
*/
public interface MsgImgMapper extends BaseMapper<MsgImg> {

    List<String> getHistory(@Param("sevenDayAgo") LocalDateTime sevenDayAgo);

    Boolean deleteBatchNames(@Param("deleteSuccess") List<String> deleteSuccess);
}




