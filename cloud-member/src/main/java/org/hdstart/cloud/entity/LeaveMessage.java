package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName leave_message
 */
@TableName(value ="leave_message")
@Data
public class LeaveMessage {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer memberId;

    private Integer leaveId;

    /**
     * 
     */
    private String textContent;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}