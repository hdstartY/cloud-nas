package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName point_message
 */
@TableName(value ="point_message")
@Data
public class PointMessage implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer sendId;

    /**
     * 
     */
    private Integer recieveId;

    /**
     * 
     */
    private String textContent;

    /**
     * 
     */
    private Integer isRead;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}