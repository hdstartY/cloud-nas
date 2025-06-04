package org.hdstart.cloud.controller;

import jakarta.validation.Valid;
import org.hdstart.cloud.entity.Comment;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.CommentService;
import org.hdstart.cloud.vo.ShowCommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment/")
public class CommentController {


    @Autowired
    private CommentService commentService;

    @GetMapping("listByBlogId")
    public Result<List<ShowCommentVo>> listByBlogId(@RequestParam("id") Integer id,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "5") Integer pageSize,
                               @RequestParam(value = "currentPage",required = false,defaultValue = "1") Integer currentPage) {

        List<ShowCommentVo> results = commentService.listByBlogId(id,currentPage,pageSize);
        return Result.success(results);
    }

    @PostMapping("save")
    public Result<Map<String,String>> save(@Valid @RequestBody Comment comment) {
        boolean isSuccess = commentService.save(comment);
        return Result.success("msg",isSuccess? "评论成功" : "评论失败");
    }
}
