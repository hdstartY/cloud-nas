package org.hdstart.cloud.to;

import lombok.Data;
import org.hdstart.cloud.elasticsearch.annotation.ESField;
import org.hdstart.cloud.elasticsearch.constant.ESFieldType;

@Data
public class ImgTo {

    @ESField(type = ESFieldType.KEYWORD)
    private String preUrl;

    @ESField(type = ESFieldType.KEYWORD)
    private String oriUrl;

    @ESField(type = ESFieldType.KEYWORD)
    private Integer isVideo;
}
