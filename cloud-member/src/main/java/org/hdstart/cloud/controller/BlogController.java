package org.hdstart.cloud.controller;

import org.hdstart.cloud.dto.BlogFile;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.BlogService;
import org.hdstart.cloud.vo.ShowBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/blog/")
public class BlogController {


    @Autowired
    private BlogService blogService;

    @PostMapping("publishBlog")
    public Result<Map<String,String>> publicBlog (@RequestParam("memberId") Integer memberId,
                                                  @RequestParam("isPublic") Integer isPublic,
                                                  @RequestParam(value = "textContent",required = false) String textContent,
                                                  @RequestParam(value = "images",required = false) List<MultipartFile> images) {

        if (memberId == null) {
            return Result.error("msg","未知错误，登录状态可能异常");
        }
        if (isPublic == null) {
            return Result.error("msg","博客状态设置异常");
        }
        if (textContent.isEmpty() && images == null) {
            return Result.error("msg","不能发表空内容");
        }
        Result<Map<String,String>> result = blogService.publishBlog(new BlogFile(memberId,isPublic,textContent,images));
        return result;
    }

    @GetMapping("getBlogsByMemberId")
    public Result<List<ShowBlogVo>> getBlogsByMemberId (@RequestParam("memberId") Integer memberId) {
        List<ShowBlogVo> blogs = blogService.getBlogsByMemberId(memberId);
        return Result.success(blogs);
    }

    @GetMapping("getBlogById")
    public Result<ShowBlogVo> getBlogById (@RequestParam("blogId") Integer blogId) {
        ShowBlogVo blogs = blogService.getBlogById(blogId);
        return Result.success(blogs);
    }

    @GetMapping("list")
    public Result<List<ShowBlogVo>> listBlogs (@RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage,
                                               @RequestParam(value = "pageSize",required = false,defaultValue = "40") Integer pageSize,
                                               @RequestParam(value = "orderType") String orderType) throws ExecutionException, InterruptedException {

        List<ShowBlogVo> showBlogVoList = blogService.listShowBlogs(currentPage,pageSize,orderType);
        return Result.success(showBlogVoList);
    }
}
