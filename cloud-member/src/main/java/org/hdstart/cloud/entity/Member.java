package org.hdstart.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 
 * @TableName member
 */
@TableName(value ="member")
@Data
@ToString
@EqualsAndHashCode
public class Member {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 8, message = "昵称长度必须在1到8个字符之间")
    private String nickName;

    /**
     * 
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须是11位数字")
    private String phone;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8,message = "密码至少为8位")
    private String password;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String avatar;
    /**
     * 
     */
    @TableLogic
    private Integer isDeleted;

}