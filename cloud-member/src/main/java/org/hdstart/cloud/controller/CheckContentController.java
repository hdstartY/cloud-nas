package org.hdstart.cloud.controller;


import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.BlogService;
import org.hdstart.cloud.vo.CheckContentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/check/")
public class CheckContentController {

    @Autowired
    private BlogService blogService;

    @GetMapping("getCheckContent")
    public Result getCheckContent (@RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {

        List<CheckContentVo> vos = blogService.getCheckContent(currentPage,pageSize);
        return Result.success(vos);
    }

    @GetMapping("passContent")
    public Result passContent (@RequestParam(value = "blogId") Integer blogId) {

        Boolean isSuccess = blogService.passContent(blogId);
        if (isSuccess) {
            return Result.success("成功");
        }
        return Result.build(500,"有同步任务执行中...请稍后再试",null);
    }
}
