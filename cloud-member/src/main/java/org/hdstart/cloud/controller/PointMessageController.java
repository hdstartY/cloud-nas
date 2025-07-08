package org.hdstart.cloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.entity.PointMessage;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.PointMessageService;
import org.hdstart.cloud.vo.HistoryPointMessageVo;
import org.hdstart.cloud.vo.PointMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pointMessage/")
public class PointMessageController {


    @Autowired
    private PointMessageService pointMessageService;

    @GetMapping("storeMessage")
    public Result<Boolean> storeMessage (@RequestParam(value = "sendId")Integer sendId,
                                 @RequestParam(value = "receiveId") Integer receiveId,
                                 @RequestParam(value = "message") String message,
                                 @RequestParam(value = "isRead") Integer isRead) {

        log.info("执行持久化");
        try {
            PointMessage pointMessage = new PointMessage();
            pointMessage.setSendId(sendId);
            pointMessage.setRecieveId(receiveId);
            pointMessage.setTextContent(message);
            pointMessage.setIsRead(isRead);

            boolean saved = pointMessageService.save(pointMessage);
            if (!saved) {
                log.error("保存失败：{}", pointMessage);
                return Result.build(400,"保存失败",null);
            }

            log.info("消息持久化完成：{}", pointMessage);
            return Result.success(saved);
        } catch (Exception e) {
            log.error("保存消息时异常", e);
            return Result.build(500,"保存异常：" + e.getMessage(),null);
        }
    }

    @GetMapping("getNotRedMessages")
    public Result<List<PointMessageVo>> getNotRedMessages(@RequestParam(value = "memberId") Integer memberId) {

       List<PointMessageVo> pointMessageVos = pointMessageService.getNotRedMessages(memberId);

       return Result.success(pointMessageVos);
    }

    @GetMapping("getHistory")
    public Result<List<HistoryPointMessageVo>> getHistory (@RequestParam(value = "sendId") Integer sendId,
                                                           @RequestParam(value = "receiveId") Integer receiveId,
                                                           @RequestParam(value = "currentPage") Integer currentPage) {

        List<HistoryPointMessageVo> vos = pointMessageService.getHistory(sendId,receiveId,currentPage);
        return Result.success(vos);
    }
}
