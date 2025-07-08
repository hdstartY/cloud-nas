package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.async.PointMessageAsyncService;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.entity.PointMessage;
import org.hdstart.cloud.mapper.MemberMapper;
import org.hdstart.cloud.service.PointMessageService;
import org.hdstart.cloud.mapper.PointMessageMapper;
import org.hdstart.cloud.vo.HistoryPointMessageVo;
import org.hdstart.cloud.vo.PointMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author 32600
* @description 针对表【point_message】的数据库操作Service实现
* @createDate 2025-06-15 14:56:29
*/
@Service
public class PointMessageServiceImpl extends ServiceImpl<PointMessageMapper, PointMessage>
    implements PointMessageService{

    @Autowired
    private PointMessageMapper pointMessageMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PointMessageAsyncService pointMessageAsyncService;

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

}




