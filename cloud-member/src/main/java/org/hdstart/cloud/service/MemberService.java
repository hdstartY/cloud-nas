package org.hdstart.cloud.service;

import org.hdstart.cloud.dto.MemberDTO;
import org.hdstart.cloud.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.vo.PublisherInfoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author 32600
* @description 针对表【member】的数据库操作Service
* @createDate 2025-05-27 22:17:53
*/
public interface MemberService extends IService<Member> {

    List<Integer> getFollowedMembersIds(Integer followerId);

    String updateAvatar(Integer memberId,String avatarUrl, MultipartFile imgFile);

    String updateBackImg(Integer memberId, String backImgUrl, MultipartFile imgFile);

    PublisherInfoVo getPublisherInfo(Integer memberId);

    MemberDTO getMemberInfo(Integer id);

    String getMemberNickName(Integer id);
}
