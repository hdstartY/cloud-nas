package org.hdstart.cloud.async;

import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.mapper.PointMessageMapper;
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
}
