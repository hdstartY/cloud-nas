package org.hdstart.cloud.controller;

import org.apache.catalina.LifecycleState;
import org.hdstart.cloud.entity.LeaveMessage;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.LeaveMessageService;
import org.hdstart.cloud.vo.LeaveMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leave/")
public class LeaveMessageController {

    @Autowired
    private LeaveMessageService leaveMessageService;

    @GetMapping("listLeaveMessage")
    public Result listLeaveMessage(@RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "20") Integer pageSize,
                                   @RequestParam("memberId") Integer memberId) {

        List<LeaveMessageVo> leaveMessageVos =  leaveMessageService.listLeaveMessage(currentPage,pageSize,memberId);

        return Result.success(leaveMessageVos);
    }

    @GetMapping("leaveMessage")
    public Result leaveMessage(@RequestParam("leaveId") Integer leaveId,
                               @RequestParam("memberId") Integer memberId,
                               @RequestParam("textContent") String textContent) {

        LeaveMessage leaveMessage = new LeaveMessage();
        leaveMessage.setLeaveId(leaveId);
        leaveMessage.setMemberId(memberId);
        leaveMessage.setTextContent(textContent);
        boolean isSuccess = leaveMessageService.save(leaveMessage);
        if (isSuccess) {
            return Result.success("评论成功");
        }

        return Result.build(500,"评论失败",null);
    }
}
