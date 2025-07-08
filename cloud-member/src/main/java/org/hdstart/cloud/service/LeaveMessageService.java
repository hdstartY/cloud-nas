package org.hdstart.cloud.service;

import org.hdstart.cloud.entity.LeaveMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.vo.LeaveMessageVo;

import java.util.List;

/**
* @author 32600
* @description 针对表【leave_message】的数据库操作Service
* @createDate 2025-06-03 15:00:08
*/
public interface LeaveMessageService extends IService<LeaveMessage> {

    List<LeaveMessageVo> listLeaveMessage(Integer currentPage, Integer pageSize, Integer memberId);
}
