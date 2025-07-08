package org.hdstart.cloud.controller;

import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.OtherMemberInfoService;
import org.hdstart.cloud.vo.OtherInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otherInfo/")
public class OtherMemberInfoController {


    @Autowired
    private OtherMemberInfoService otherMemberInfoService;

    @PostMapping("changeOtherInfo")
    public Result changeOtherInfo(@RequestBody OtherInfoVo otherInfoVo) {
        Result isSuccess = otherMemberInfoService.updateOtherInfo(otherInfoVo);
        return isSuccess;
    }
}
