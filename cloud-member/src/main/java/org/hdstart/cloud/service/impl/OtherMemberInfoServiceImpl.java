package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.entity.OtherMemberInfo;
import org.hdstart.cloud.mapper.MemberMapper;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.OtherMemberInfoService;
import org.hdstart.cloud.mapper.OtherMemberInfoMapper;
import org.hdstart.cloud.vo.OtherInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 32600
* @description 针对表【other_member_info】的数据库操作Service实现
* @createDate 2025-06-03 11:52:05
*/
@Service
public class OtherMemberInfoServiceImpl extends ServiceImpl<OtherMemberInfoMapper, OtherMemberInfo>
    implements OtherMemberInfoService{

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private OtherMemberInfoMapper otherMemberInfoMapper;

    @Transactional
    @Override
    public Result updateOtherInfo(OtherInfoVo otherInfoVo) {
        if (otherInfoVo.getMemberId() == null) {
            return Result.build(400,"用户信息为空",null);
        }
        if (otherInfoVo.getNickName() != null && !otherInfoVo.getNickName().isEmpty()) {
            if (otherInfoVo.getNickName().length() > 8 || otherInfoVo.getNickName().length() < 1) {
                return Result.build(400,"昵称长度不能超过8个字符",null);
            }
            Member member = new Member();
            member.setId(otherInfoVo.getMemberId());
            member.setNickName(otherInfoVo.getNickName());
            int isSave = memberMapper.update(member, new QueryWrapper<Member>().eq("id", otherInfoVo.getMemberId()));
        }

        OtherMemberInfo otherMemberInfo = new OtherMemberInfo();
        otherMemberInfo.setMemberId(otherInfoVo.getMemberId());
        if (otherInfoVo.getSignature() != null && !otherInfoVo.getSignature().isEmpty()) {
            if (otherInfoVo.getSignature().length() > 20) {
                return Result.build(400,"签名长度不能超过20个字符",null);
            }
            otherMemberInfo.setSignature(otherInfoVo.getSignature());
        }
        if (otherInfoVo.getOther() != null && !otherInfoVo.getOther().isEmpty()) {
            if (otherInfoVo.getSignature().length() > 20) return Result.build(400,"其他信息长度不能超过20个字符",null);;
            otherMemberInfo.setOther(otherInfoVo.getOther());
        }
        if (otherMemberInfo.getSignature() != null || otherMemberInfo.getOther() != null) {
            int isSuccess = otherMemberInfoMapper.update(otherMemberInfo, new QueryWrapper<OtherMemberInfo>().eq("member_id", otherInfoVo.getMemberId()));
        }
        return Result.success(null);
    }
}




