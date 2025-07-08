package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.entity.LeaveMessage;
import org.hdstart.cloud.service.LeaveMessageService;
import org.hdstart.cloud.mapper.LeaveMessageMapper;
import org.hdstart.cloud.vo.LeaveMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 32600
* @description 针对表【leave_message】的数据库操作Service实现
* @createDate 2025-06-03 15:00:08
*/
@Service
public class LeaveMessageServiceImpl extends ServiceImpl<LeaveMessageMapper, LeaveMessage>
    implements LeaveMessageService{

    @Autowired
    private LeaveMessageMapper leaveMessageMapper;

    @Override
    public List<LeaveMessageVo> listLeaveMessage(Integer currentPage, Integer pageSize, Integer memberId) {

        List<LeaveMessageVo> leaveMessageVos = leaveMessageMapper.listLeaveMessage((currentPage - 1) * pageSize,pageSize,memberId);
        return leaveMessageVos;
    }
}




