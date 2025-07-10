package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.PointMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hdstart.cloud.vo.HistoryPointMessageVo;
import org.hdstart.cloud.vo.PointMessageVo;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 32600
* @description 针对表【point_message】的数据库操作Mapper
* @createDate 2025-06-15 14:56:29
* @Entity org.hdstart.cloud.entity.PointMessage
*/
public interface PointMessageMapper extends BaseMapper<PointMessage> {

    List<PointMessageVo> getNewNumByMemberId(@Param("memberId") Integer memberId);

    List<HistoryPointMessageVo> getHistory(@Param("sendId") Integer sendId, @Param("receiveId") Integer receiveId, @Param("currentPage") Integer currentPage);

    void setReadStatus(@Param("ids") List<Integer> ids);

    List<Integer> selectRecently(@Param("memberId") Integer memberId);

    Boolean cleanChatHistory(@Param("now") LocalDateTime now, @Param("sevenDayAgo") LocalDateTime sevenDayAgo);
}




