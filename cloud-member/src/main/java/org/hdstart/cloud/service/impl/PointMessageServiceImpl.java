package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.async.PointMessageAsyncService;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.entity.PointMessage;
import org.hdstart.cloud.mapper.MemberMapper;
import org.hdstart.cloud.mapper.MsgImgMapper;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.MsgImgService;
import org.hdstart.cloud.service.PointMessageService;
import org.hdstart.cloud.mapper.PointMessageMapper;
import org.hdstart.cloud.utils.minio.fileType.FileType;
import org.hdstart.cloud.utils.minio.utils.MinioUtils;
import org.hdstart.cloud.vo.HistoryPointMessageVo;
import org.hdstart.cloud.vo.PointMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 32600
* @description 针对表【point_message】的数据库操作Service实现
* @createDate 2025-06-15 14:56:29
*/
@Slf4j
@Service
public class PointMessageServiceImpl extends ServiceImpl<PointMessageMapper, PointMessage>
    implements PointMessageService{

    @Autowired
    private PointMessageMapper pointMessageMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PointMessageAsyncService pointMessageAsyncService;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MsgImgMapper msgImgMapper;

    @Override
    public List<PointMessageVo> getNotRedMessages(Integer memberId) {

        List<Integer> sendIds = pointMessageMapper.selectRecently(memberId);
        Map<Integer, Integer> sendIdMapNewsNum = pointMessageMapper.getNewNumByMemberId(memberId).stream().collect(Collectors.toMap(item -> item.getMemberId(), item -> item.getNewsNum()));

        List<Member> sendMembers = memberMapper.selectBatchIds(sendIds);
        Map<Integer, Member> idMapMember = sendMembers.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));

        List<PointMessageVo> pointMessageVoList = sendIds.stream().map(item -> {
            Member member = idMapMember.get(item);
            PointMessageVo pointMessageVo = new PointMessageVo();
            pointMessageVo.setMemberId(item);
            pointMessageVo.setNickName(member.getNickName());
            pointMessageVo.setAvatar(member.getAvatar());
            Integer newsNum = sendIdMapNewsNum.get(item);
            if (newsNum != null) {
                pointMessageVo.setNewsNum(newsNum);
            } else {
                pointMessageVo.setNewsNum(0);
            }
            return pointMessageVo;
        }).collect(Collectors.toList());

        return pointMessageVoList;
    }

    @Override
    public List<HistoryPointMessageVo> getHistory(Integer sendId, Integer receiveId, Integer currentPage) {


        List<HistoryPointMessageVo> vos = pointMessageMapper.getHistory(sendId,receiveId,(currentPage - 1) * 20);
        Collections.reverse(vos);
        pointMessageAsyncService.setMessageIsRead(vos);
        return vos;
    }

    @Override
    public Boolean cleanChatHistory() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDayAgo = now.minusDays(7);
        Boolean isSuccess = pointMessageMapper.cleanChatHistory(now,sevenDayAgo);
        return isSuccess;
    }

    @Override
    public Boolean cleanChatImg() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDayAgo = now.minusDays(7);
        List<String> objectNames = msgImgMapper.getHistory(sevenDayAgo);
        if (objectNames == null || objectNames.isEmpty()) {
            return false;
        }
        List<String> deleteSuccess = new ArrayList<>();
        objectNames.stream().forEach(item -> {
            try {
                minioUtils.deleteFile(item);
                deleteSuccess.add(item);
            } catch (Exception e) {
                log.error("有图片删除失败:" + item);
            }
        });

        Boolean isSuccess = msgImgMapper.deleteBatchNames(deleteSuccess);
        log.info("图片清理完成...");
        return isSuccess;
    }

    @Override
    public Result uploadImg(List<MultipartFile> images) {

        if (images == null && images.isEmpty()) {
            return Result.build(500,"发送文件为空！！！",null);
        }
        List<String> objectNames = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();
        images.stream().forEach(item -> {
            try {
                String objectName = minioUtils.uploadFile(item, FileType.MSG_IMG);
                objectNames.add(objectName);
                imgUrls.add(minioUtils.getPreviewUrl(objectName));
            } catch (Exception e) {
                log.error("部分聊天图片上传失败：" + e.getMessage());
            } finally {
                pointMessageAsyncService.saveMsgImgName(objectNames);
            }
        });

        return Result.success(imgUrls);
    }
}




