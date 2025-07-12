package org.hdstart.cloud.elasticsearch.entity;

import lombok.Data;
import org.hdstart.cloud.elasticsearch.annotation.ESField;
import org.hdstart.cloud.constant.es.ESFieldType;

@Data
public class ESMemberInfo {

    @ESField(type = ESFieldType.KEYWORD)
    private String id;

    @ESField(type = ESFieldType.TEXT)
    private String nickName;

    @ESField(type = ESFieldType.KEYWORD)
    private String avatar;
}
