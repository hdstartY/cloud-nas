package org.hdstart.cloud.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "memberInfo")
@NoArgsConstructor
public class ESMemberInfo {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String nickName;
}
