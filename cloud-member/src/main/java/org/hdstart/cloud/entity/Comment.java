package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 
 * @TableName comment
 */
@TableName(value ="comment")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class Comment {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "评论用户信息不能为空")
    private Integer memberId;

    @NotNull(message = "所评论博客信息不能为空")
    private Integer blogId;

    @NotBlank(message = "评论内容不能为空")
    private String textContent;

    private Integer likeNum;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}