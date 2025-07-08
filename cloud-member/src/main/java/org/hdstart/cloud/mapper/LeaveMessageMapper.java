package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.LeaveMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hdstart.cloud.vo.LeaveMessageVo;

import java.util.List;

/**
* @author 32600
* @description 针对表【leave_message】的数据库操作Mapper
* @createDate 2025-06-03 15:00:08
* @Entity org.hdstart.cloud.entity.LeaveMessage
*/
public interface LeaveMessageMapper extends BaseMapper<LeaveMessage> {

    List<LeaveMessageVo> listLeaveMessage(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("memberId") Integer memberId);
}




