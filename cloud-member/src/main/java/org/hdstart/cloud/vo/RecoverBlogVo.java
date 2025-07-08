package org.hdstart.cloud.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecoverBlogVo {

    private Integer id;
    private String avatar;
    private String textContent;
    private LocalDateTime createTime;
    private List<String> images;
}
