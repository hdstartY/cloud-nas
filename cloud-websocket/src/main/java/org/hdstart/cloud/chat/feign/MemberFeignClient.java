package org.hdstart.cloud.chat.feign;

import org.hdstart.cloud.dto.MemberDTO;
import org.hdstart.cloud.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cloud-nas-member")
public interface MemberFeignClient {


    @GetMapping("/member/getMemberInfo/{id}")
    MemberDTO getMemberInfo (@PathVariable("id") Integer id);

    @GetMapping("/pointMessage/storeMessage")
    Result<Boolean> storeMessage (@RequestParam(value = "sendId")Integer sendId,
                                         @RequestParam(value = "receiveId") Integer receiveId,
                                         @RequestParam(value = "message") String message,
                                         @RequestParam(value = "isRead") Integer isRead);

    @GetMapping("/member/getMemberNickName")
    String getMemberNickName(@RequestParam("memberId") Integer id);
}
