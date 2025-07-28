package org.hdstart.cloud.vo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class BlogImgUrlVo {

    private Integer blogId;

    private String preUrl;

    private String oriUrl;

    private Integer isVideo;
}
