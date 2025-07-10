package org.hdstart.cloud.async;

import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.entity.MsgImg;
import org.hdstart.cloud.mapper.PointMessageMapper;
import org.hdstart.cloud.service.MsgImgService;
import org.hdstart.cloud.vo.HistoryPointMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PointMessageAsyncService {

    @Autowired
    private PointMessageMapper pointMessageMapper;

    @Autowired
    private MsgImgService msgImgService;

    @Async
    public void setMessageIsRead(List<HistoryPointMessageVo> vos) {
        log.info("异步设置消息状态");
        List<Integer> ids = vos.stream().filter(item -> {
            return item.getIsRead() == 0;
        }).collect(Collectors.toList()).stream().map(item -> {
            Integer id = item.getId();
            return id;
        }).collect(Collectors.toList());

        if (ids.isEmpty()) {
            log.info("无未读，停止");
            return;
        }
        pointMessageMapper.setReadStatus(ids);
        log.info("消息状态设置完毕");
    }

    @Async
    public void saveMsgImgName (List<String> objectNames) {
        log.info("开始同步聊天图片到数据库");
        if (objectNames == null || objectNames.isEmpty()) {
            log.info("聊天列表为空，停止同步");
            return;
        }
        List<MsgImg> msgImgs = objectNames.stream().map(item -> {
            MsgImg msgImg = new MsgImg();
            msgImg.setImgUrl(item);
            return msgImg;
        }).collect(Collectors.toList());

        msgImgService.saveBatch(msgImgs);

        log.info("聊天图片同步完成");
    }
}
