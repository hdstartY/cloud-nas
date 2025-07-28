package org.hdstart.cloud.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.dto.MemberDTO;
import org.hdstart.cloud.entity.Member;
import org.hdstart.cloud.entity.OtherMemberInfo;
import org.hdstart.cloud.result.RE;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.MemberService;
import org.hdstart.cloud.service.OtherMemberInfoService;
import org.hdstart.cloud.utils.JwtUtils;
import org.hdstart.cloud.elasticsearch.entity.ESMemberInfo;
import org.hdstart.cloud.vo.MemberVo;
import org.hdstart.cloud.vo.PublisherInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/member/")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private OtherMemberInfoService otherMemberInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @PostMapping("loginByPhone")
    public Result LoginByPhone (@RequestBody Member member) {
        if (member.getPhone().isEmpty() || member.getPassword().isEmpty()) {
            return Result.error(RE.USER_PHONE_EMPTY);
        }
        Member storeMember = memberService.getOne(new QueryWrapper<Member>().eq("phone", member.getPhone()));
        if (storeMember == null) {
            return Result.error(RE.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(member.getPassword(),storeMember.getPassword())) {
            return Result.error(RE.USER_PASSWORD_ERROR);
        }

        String token = JwtUtils.generateToken(storeMember.getId().toString(), storeMember.getPhone());
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("id",storeMember.getId().toString());
        result.put("avatarUrl",storeMember.getAvatar());
        result.put("nickName",storeMember.getNickName());
        stringRedisTemplate.opsForHash().put("tokens",storeMember.getId().toString(),token);
        return Result.build(RE.USER_LOGIN_SUCCESS,result);
    }

    @PostMapping("loginByEmail")
    public Result LoginByEmail (@RequestBody Member member) {
        if (member.getEmail().isEmpty() || member.getPassword().isEmpty()) {
            return Result.error(RE.USER_EMAIL_EMPTY);
        }
        Member storeMember = memberService.getOne(new QueryWrapper<Member>().eq("email", member.getEmail()));
        if (storeMember == null) {
            return Result.error(RE.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(member.getPassword(),storeMember.getPassword())) {
            return Result.error(RE.USER_PASSWORD_ERROR);
        }
        String token = JwtUtils.generateToken(storeMember.getId().toString(), storeMember.getPhone());
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("id",storeMember.getId().toString());
        result.put("avatarUrl",storeMember.getAvatar());
        result.put("nickName",storeMember.getNickName());
        stringRedisTemplate.opsForHash().put("tokens",storeMember.getId().toString(),token);
        return Result.build(RE.USER_LOGIN_SUCCESS,result);
    }

    @GetMapping("list")
    public Result<List<Member>> listMember () {
        List<Member> members = memberService.list();
        return Result.success(members);
    }

    @PostMapping("saveMember")
    public Result saveMember (@Valid @RequestBody Member member) {
        // 重复校验
        if (memberService.exists(new QueryWrapper<Member>().eq("phone", member.getPhone()))) {
            return Result.error(RE.USER_REGISTER_FAILD);
        }
        if (!member.getEmail().isEmpty()) {
            if (memberService.exists(new QueryWrapper<Member>().eq("email", member.getEmail()))) {
                return Result.error(RE.USER_REGISTER_FAILD);
            }
        }
        //加密密码
        String encodePassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodePassword);
        // 保存用户
        boolean isSuccess = memberService.save(member);
        if (!isSuccess) {
            return Result.error(RE.USER_REGISTER_FAILD);
        }
        Member storeMember = memberService.getById(member.getId());
        //保存es
        ESMemberInfo esMemberInfo = new ESMemberInfo();
        esMemberInfo.setId(member.getId().toString());
        esMemberInfo.setNickName(member.getNickName());
        esMemberInfo.setAvatar(storeMember.getAvatar());
        try {
            log.info("保存用户信息到ES中...");
            elasticsearchClient.index(i -> i
                    .index("memberinfo")
                    .id(member.getId().toString())
                    .document(esMemberInfo)
            );
            log.info("用户信息成功保存到ES中...");
        } catch (IOException e) {
            log.error("用户数据保存ES失败...");
        }

        //其他数据
        OtherMemberInfo otherMemberInfo = new OtherMemberInfo();
        otherMemberInfo.setMemberId(member.getId());
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

    @GetMapping("getMemberInfo/{id}")
    public MemberDTO getMemberInfo (@PathVariable("id") Integer id) {
        MemberDTO memberDTO = memberService.getMemberInfo(id);
        return memberDTO;
    }

    @GetMapping("getMemberNickName")
    public String getMemberNickName(@RequestParam("memberId") Integer id) {

        String nickName = memberService.getMemberNickName(id);
        return nickName;
    }

    @GetMapping("searchByES")
    public Result<List<ESMemberInfo>> searchByES(@RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("searchValue") String searchValue) {

        Result<List<ESMemberInfo>> result = memberService.searchByES(currentPage,pageSize,searchValue);
        return result;
    }

    @GetMapping("getById")
    public Result<MemberVo> getById(@RequestParam("id") Integer id) {
        MemberVo vo = memberService.getVoById(id);
        return Result.success(vo);
    }
}
