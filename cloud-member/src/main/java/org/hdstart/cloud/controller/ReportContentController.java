package org.hdstart.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.hdstart.cloud.entity.BlogReport;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.BlogReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report/")
public class ReportContentController {

    @Autowired
    private BlogReportService blogReportService;

    @GetMapping("reportById")
    public Result reportById (@RequestParam(value = "blogId") Integer blogId,
                              @RequestParam(value = "memberId") Integer memberId) {

        BlogReport storeReport = blogReportService.getOne(new QueryWrapper<BlogReport>().eq("blog_id", blogId).eq("member_id", memberId));
        if (storeReport != null) {
            return Result.build(400,"你已举报过该博客",null);
        }
        BlogReport blogReport = new BlogReport();
        blogReport.setBlogId(blogId);
        blogReport.setMemberId(memberId);
        blogReportService.save(blogReport);
        return Result.success("举报成功");
    }
}
