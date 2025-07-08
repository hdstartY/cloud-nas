package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 
 * @TableName blog
 */
@TableName(value ="blog")
@Data
@ToString
@EqualsAndHashCode
public class Blog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer memberId;

    private String textContent;

    private Integer likeNum;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime deletedTime;

    private Integer isPublic;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private List<String> images;


}