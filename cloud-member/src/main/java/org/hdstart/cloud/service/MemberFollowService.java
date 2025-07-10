package org.hdstart.cloud.service;

import org.hdstart.cloud.entity.MemberFollow;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.vo.FanCountsVo;
import org.hdstart.cloud.vo.FollowingMemberVo;
import org.hdstart.cloud.vo.ShowBlogVo;

import java.util.List;

/**
* @author 32600
* @description 针对表【member_follow】的数据库操作Service
* @createDate 2025-06-01 12:29:00
*/
public interface MemberFollowService extends IService<MemberFollow> {

    List<ShowBlogVo> listFollowedBlogs(Integer currentPage, Integer pageSize, List<Integer> followedIds, String orderType);

    FanCountsVo getFansCounts(Integer memberId);

    Result<List<FollowingMemberVo>> getFollowingMembers(Integer memberId, Integer currentPage, Integer pageSize);
}
