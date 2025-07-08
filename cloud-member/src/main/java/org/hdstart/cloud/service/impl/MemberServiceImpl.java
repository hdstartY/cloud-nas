package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.dto.MemberDTO;
import org.hdstart.cloud.entity.Blog;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.entity.MemberFollow;
import org.hdstart.cloud.entity.OtherMemberInfo;
import org.hdstart.cloud.mapper.BlogMapper;
import org.hdstart.cloud.mapper.MemberFollowMapper;
import org.hdstart.cloud.mapper.OtherMemberInfoMapper;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.MemberService;
import org.hdstart.cloud.mapper.MemberMapper;
import org.hdstart.cloud.utils.minio.fileType.FileType;
import org.hdstart.cloud.utils.minio.utils.MinioUtils;
import org.hdstart.cloud.vo.PublisherInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author 32600
* @description 针对表【member】的数据库操作Service实现
* @createDate 2025-05-27 22:17:53
*/
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member>
    implements MemberService{

    @Autowired
    private MemberFollowMapper memberFollowMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private OtherMemberInfoMapper otherMemberInfoMapper;

    @Autowired
    private MemberFollowMapper followMapper;


    @Override
    public List<Integer> getFollowedMembersIds(Integer followerId) {

        List<Integer> followedIds = memberFollowMapper.getFollowedMembersIds(followerId);
        return followedIds;
    }

    @Transactional
    @Override
    public String updateAvatar(Integer memberId, String avatarUrl,MultipartFile avatarFile) {
        String fileObjectName = "";
        int index = 0,count = 0;
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            for (int i = 0; i <avatarUrl.length(); i++) {
                char c = avatarUrl.toCharArray()[i];
                if (c == '/') {
                    count += 1;
                }
                if (count == 4) {
                    index = i;
                    break;
                }
            }
            avatarUrl = avatarUrl.substring(index+1);
        }
        String previewUrl = "";
        try {
            fileObjectName = minioUtils.uploadFile(avatarFile, FileType.AVATAR);
            previewUrl = minioUtils.getPreviewUrl(fileObjectName);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                minioUtils.deleteFile(avatarUrl);
            }
        } catch (Exception e) {
            return null;
        }
        Member member = new Member();
        member.setAvatar(previewUrl);
        member.setId(memberId);
        try {
            memberMapper.update(member,new QueryWrapper<Member>().eq("id", memberId));
            return previewUrl;
        } catch (Exception e) {
            minioUtils.deleteFile(fileObjectName);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String updateBackImg(Integer memberId, String backImgUrl, MultipartFile imgFile) {

        String fileObjectName = "";
        int index = 0,count = 0;
        if (backImgUrl != null && !backImgUrl.isEmpty()) {
            for (int i = 0; i <backImgUrl.length(); i++) {
                char c = backImgUrl.toCharArray()[i];
                if (c == '/') {
                    count += 1;
                }
                if (count == 4) {
                    index = i;
                    break;
                }
            }
            backImgUrl = backImgUrl.substring(index+1);
        }
        String previewUrl = "";
        try {
            fileObjectName = minioUtils.uploadFile(imgFile, FileType.BACK_IMG);
            previewUrl = minioUtils.getPreviewUrl(fileObjectName);
            if (backImgUrl != null && !backImgUrl.isEmpty()) {
                minioUtils.deleteFile(backImgUrl);
            }
        } catch (Exception e) {
            return null;
        }
        OtherMemberInfo otherMemberInfo = new OtherMemberInfo();
        otherMemberInfo.setMemberId(memberId);
        otherMemberInfo.setBackImg(previewUrl);
        try {
            otherMemberInfoMapper.insertOrUpdate(otherMemberInfo,memberId);
            return previewUrl;
        } catch (Exception e) {
            minioUtils.deleteFile(fileObjectName);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PublisherInfoVo getPublisherInfo(Integer memberId) {
        PublisherInfoVo publisherInfoVo = new PublisherInfoVo();
        OtherMemberInfo otherMemberInfo = otherMemberInfoMapper.selectOne(new QueryWrapper<OtherMemberInfo>().eq("member_id", memberId));
        if (otherMemberInfo != null) {
            BeanUtils.copyProperties(otherMemberInfo, publisherInfoVo);
        }
        Long followCounts = memberFollowMapper.selectCount(new QueryWrapper<MemberFollow>().eq("follower_id", memberId));
        Long fanCounts = memberFollowMapper.selectCount(new QueryWrapper<MemberFollow>().eq("followed_id", memberId));
        publisherInfoVo.setFollowCounts(followCounts);
        publisherInfoVo.setFanCounts(fanCounts);
        return publisherInfoVo;
    }

    @Override
    public MemberDTO getMemberInfo(Integer id) {

        MemberDTO memberDTO = memberMapper.getMemberInfo(id);
        return memberDTO;
    }

    @Override
    public String getMemberNickName(Integer id) {

        String nickName =  memberMapper.getMemberNickName(id);
        return nickName;
    }

}




