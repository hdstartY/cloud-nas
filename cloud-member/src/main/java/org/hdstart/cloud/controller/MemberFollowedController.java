package org.hdstart.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.hdstart.cloud.entity.MemberFollow;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.MemberFollowService;
import org.hdstart.cloud.service.MemberService;
import org.hdstart.cloud.to.FollowedParamTo;
import org.hdstart.cloud.vo.FanCountsVo;
import org.hdstart.cloud.vo.FollowingMemberVo;
import org.hdstart.cloud.vo.ShowBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow/")
public class MemberFollowedController {


    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberFollowService memberFollowService;

    @GetMapping("getFollowedIdsById")
    public Result<List<Integer>> getFollowedMembersIds(@RequestParam("followerId") Integer followingId) {

        List<Integer> followedIds = memberService.getFollowedMembersIds(followingId);
        return Result.success(followedIds);
    }

    @GetMapping("toFollow")
    public Result toFollow(@RequestParam("followerId") Integer followerId,
                           @RequestParam("followedId") Integer followedId) {

        if (followerId == null || followedId == null) {
            return Result.build(500,"关注失败",null);
        }
        MemberFollow memberFollow = new MemberFollow();
        memberFollow.setFollowerId(followerId);
        memberFollow.setFollowedId(followedId);
        boolean isSuccess = memberFollowService.save(memberFollow);
        if (isSuccess) {
            return Result.success(isSuccess? "关注成功" : "关注失败");
        }
        return Result.build(500,"关注失败",null);
    }

    @GetMapping("toCancelFollow")
    public Result toCancelFollow(@RequestParam("followerId") Integer followerId,
                           @RequestParam("followedId") Integer followedId) {

        if (followerId == null || followedId == null) {
            return Result.build(500,"关注失败",null);
        }
        boolean isSuccess = memberFollowService.remove(new QueryWrapper<MemberFollow>().eq("follower_id", followerId).eq("followed_id", followedId));
        if (isSuccess) {
            return Result.success("取消关注成功");
        }
        return Result.build(500,"取消失败",null);
    }

    @PostMapping("listFollowed")
    public Result listFollowedBlogs (@RequestBody FollowedParamTo followedParamTo) {
        System.out.println(followedParamTo);
        List<ShowBlogVo> showBlogVoList = memberFollowService.listFollowedBlogs(followedParamTo.getCurrentPage(),
                followedParamTo.getPageSize(),followedParamTo.getFollowedId(),followedParamTo.getOrderType());
        return Result.success(showBlogVoList);
    }

    @GetMapping("getFansCountsAndOther")
    public Result getFansCounts(@RequestParam("memberId") Integer memberId) {
        FanCountsVo fanCountsVo = memberFollowService.getFansCounts(memberId);
        return Result.success(fanCountsVo);
    }

    @GetMapping("getFollowingMembers")
    public Result<List<FollowingMemberVo>> getFollowingMembers (@RequestParam("memberId") Integer memberId,
                                       @RequestParam("currentPage") Integer currentPage,
                                       @RequestParam("pageSize") Integer pageSize) {

        Result<List<FollowingMemberVo>> vos = memberFollowService.getFollowingMembers(memberId,currentPage,pageSize);
        return vos;
    }

}
