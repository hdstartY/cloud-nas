package org.hdstart.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.entity.OtherMemberInfo;
import org.hdstart.cloud.result.RE;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.MemberService;
import org.hdstart.cloud.service.OtherMemberInfoService;
import org.hdstart.cloud.utils.JwtUtils;
import org.hdstart.cloud.utils.minio.utils.MinioUtils;
import org.hdstart.cloud.vo.PublisherInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/member/")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private OtherMemberInfoService otherMemberInfoService;


    @PostMapping("loginByPhone")
    public Result LoginByPhone (@RequestBody Member member) {
        if (member.getPhone().isEmpty()) {
            return Result.error(RE.USER_PHONE_EMPTY);
        }
        Member storeMember = memberService.getOne(new QueryWrapper<Member>().eq("phone", member.getPhone()));
        if (storeMember == null) {
            return Result.error(RE.USER_NOT_FOUND);
        }
        if (!storeMember.getPassword().equals(member.getPassword())) {
            return Result.error(RE.USER_PASSWORD_ERROR);
        }
        String token = JwtUtils.generateToken(storeMember.getId().toString(), storeMember.getPhone());
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("id",storeMember.getId().toString());
        result.put("avatarUrl",storeMember.getAvatar());
        result.put("nickName",storeMember.getNickName());
        return Result.build(RE.USER_LOGIN_SUCCESS,result);
    }

    @PostMapping("loginByEmail")
    public Result LoginByEmail (@RequestBody Member member) {
        if (member.getEmail().isEmpty()) {
            return Result.error(RE.USER_EMAIL_EMPTY);
        }
        Member storeMember = memberService.getOne(new QueryWrapper<Member>().eq("email", member.getEmail()));
        if (storeMember == null) {
            return Result.error(RE.USER_NOT_FOUND);
        }
        if (!storeMember.getPassword().equals(member.getPassword())) {
            return Result.error(RE.USER_PASSWORD_ERROR);
        }
        String token = JwtUtils.generateToken(storeMember.getId().toString(), storeMember.getPhone());
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("id",storeMember.getId().toString());
        result.put("avatarUrl",storeMember.getAvatar());
        return Result.build(RE.USER_LOGIN_SUCCESS,result);
    }

    @GetMapping("list")
    public Result<List<Member>> listMember () {
        List<Member> members = memberService.list();
        return Result.success(members);
    }

    @PostMapping("saveMember")
    public Result saveMember (@Valid @RequestBody Member member) {
        Member storeMemberByPhone = memberService.getOne(new QueryWrapper<Member>().eq("phone", member.getPhone()));
        if (storeMemberByPhone == null) {
            Member storeMemberByEmail = memberService.getOne(new QueryWrapper<Member>().eq("email", member.getEmail()));
            if (storeMemberByEmail != null) {
                return Result.error(RE.USER_REGISTER_FAILD);
            }
        } else {
            return Result.error(RE.USER_REGISTER_FAILD);
        }
        boolean isSuccess = memberService.save(member);
        Integer id = memberService.getOne(new QueryWrapper<Member>().eq("phone", member.getPhone())).getId();
        OtherMemberInfo otherMemberInfo = new OtherMemberInfo();
        otherMemberInfo.setMemberId(id);
        otherMemberInfoService.save(otherMemberInfo);
        return Result.success(isSuccess);
    }

    @GetMapping("getById/{id}")
    public Result<Member> getMemberById (@PathVariable("id") Long id) {
        Member member = memberService.getById(id);
        return Result.success(member);
    }

    @GetMapping("removeById/{id}")
    public Result<Boolean> removeMemberById (@PathVariable("id") Long id) {
        boolean isSuccess = memberService.removeById(id);
        return Result.success(isSuccess);
    }

    @PostMapping("updateAvatar")
    public Result updateAvatar (@RequestParam("memberId") Integer memberId,
                                @RequestParam(value = "avatarUrl",required = false) String avatarUrl,
                                @RequestParam("imgFile") MultipartFile imgFile) {

        String updateAvatarUrl = memberService.updateAvatar(memberId,avatarUrl,imgFile);
        if (updateAvatarUrl != null ) {
            return Result.success("avatarUrl",updateAvatarUrl);
        }
        return Result.build(500,"更换失败",null);
    }

    @PostMapping("updateBackImg")
    public Result updateBackImg (@RequestParam("memberId") Integer memberId,
                                @RequestParam(value = "backImgFile",required = false) String backImgUrl,
                                @RequestParam("imgFile") MultipartFile imgFile) {

        String updateBackImgUrl = memberService.updateBackImg(memberId,backImgUrl,imgFile);
        if (updateBackImgUrl != null ) {
            return Result.success("backImgUrl",updateBackImgUrl);
        }
        return Result.build(500,"更换失败",null);
    }

    @GetMapping("getPublisherInfo")
    public Result getPublisherInfo (@RequestParam("memberId") Integer memberId) {
       PublisherInfoVo publisherInfoVo =  memberService.getPublisherInfo(memberId);
       return Result.success(publisherInfoVo);
    }
}
