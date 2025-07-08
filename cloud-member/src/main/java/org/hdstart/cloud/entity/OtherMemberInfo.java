package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName other_member_info
 */
@TableName(value ="other_member_info")
@Data
public class OtherMemberInfo {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer memberId;

    /**
     * 
     */
    private String backImg;

    /**
     * 
     */
    private String signature;

    /**
     * 
     */
    private String other;
}