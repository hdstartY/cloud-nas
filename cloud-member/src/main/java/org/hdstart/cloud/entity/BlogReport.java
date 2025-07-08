package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName blog_report
 */
@TableName(value ="blog_report")
@Data
public class BlogReport implements Serializable {
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
    private Integer memberId;

    /**
     * 
     */
    private String reportContent;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}