package org.hdstart.cloud.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hdstart.cloud.constant.es.ESFieldType;
import org.hdstart.cloud.elasticsearch.annotation.ESField;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ESBlogInfo {

    @ESField(type = ESFieldType.KEYWORD)
    private String id;

    @ESField(type = ESFieldType.KEYWORD)
    private String memberId;

    @ESField(type = ESFieldType.KEYWORD)
    private String avatar;

    @ESField(type = ESFieldType.KEYWORD)
    private String nickName;

    //可全文检索，并且添加 keyword 子字段用于聚合/排序
    @ESField(type = ESFieldType.TEXT, keywordSubField = true)
    private String textContent;

    @ESField(type = ESFieldType.KEYWORD)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createTime;

    @ESField(type = ESFieldType.KEYWORD)
    private List<String> images;
}
