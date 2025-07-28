package org.hdstart.cloud.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hdstart.cloud.elasticsearch.constant.ESFieldType;
import org.hdstart.cloud.elasticsearch.annotation.ESField;
import org.hdstart.cloud.to.ImgTo;

import java.util.List;

@Data
public class ESBlogInfo {

    @ESField(type = ESFieldType.KEYWORD)
    private String id;

    @ESField(type = ESFieldType.KEYWORD)
    private String memberId;

    @ESField(type = ESFieldType.KEYWORD)
    private String avatar;

    @ESField(type = ESFieldType.TEXT)
    private String nickName;

    @ESField(type = ESFieldType.TEXT, keywordSubField = true)
    private String textContent;

    @ESField(type = ESFieldType.KEYWORD)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ESField(type = ESFieldType.OBJECT,isList = true,nested = true)
    private List<ImgTo> images;
}
