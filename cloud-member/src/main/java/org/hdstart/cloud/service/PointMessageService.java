package org.hdstart.cloud.service;

import org.hdstart.cloud.entity.PointMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.vo.HistoryPointMessageVo;
import org.hdstart.cloud.vo.PointMessageVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author 32600
* @description 针对表【point_message】的数据库操作Service
* @createDate 2025-06-15 14:56:29
*/
public interface PointMessageService extends IService<PointMessage> {

    List<PointMessageVo> getNotRedMessages(Integer memberId);

    List<HistoryPointMessageVo> getHistory(Integer sendId, Integer receiveId, Integer currentPage);

    Boolean cleanChatHistory ();

    Boolean cleanChatImg();

    Result uploadImg(List<MultipartFile> images);
}
