package org.hdstart.cloud.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.*;

/**
 *
 * @TableName comment
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowCommentVo {
    private Integer id;
    private Integer memberId;
    private String commentNickName;
    private String avatar;
    private Integer blogId;
    private String textContent;
    private Integer likeNum;
    public Integer isPublic;
    private LocalDateTime createTime;
}