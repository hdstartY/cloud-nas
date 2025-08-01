package org.hdstart.cloud.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hdstart.cloud.to.ImgTo;

/**
 *
 * @TableName blog
 */
@Data
@ToString
@EqualsAndHashCode
public class ShowBlogVo {

    private Integer id;
    public Integer memberId;
    private String avatar;
    private String textContent;
    private String nickName;
    private LocalDateTime createTime;
    private Integer isPublic;
    private Integer isDeleted;
    private Boolean isShowComment = false;
    private List<ImgTo> images;
    private Integer likeNum;
    private Long commentNum = 0L;
    private List<ShowCommentVo> comments = new ArrayList<>();
    private Boolean commentLoading = true;
}