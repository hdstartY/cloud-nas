package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName images
 */
@TableName(value ="images")
@Data
public class Images {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer blogId;

    /**
     * 
     */
    private String preUrl;
    private String oriUrl;
    private Integer isVideo;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDeleted;

    private Integer state;
}