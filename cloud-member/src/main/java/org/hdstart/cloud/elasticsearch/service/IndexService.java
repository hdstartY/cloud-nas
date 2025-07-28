package org.hdstart.cloud.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.elasticsearch.annotation.ESField;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class IndexService {

    private final ElasticsearchClient client;

    public IndexService(ElasticsearchClient client) {
        this.client = client;
    }

    public void safeCreateIndex(String indexName, Class<?> clazz) {
        try {
            boolean exists = client.indices().exists(e -> e.index(indexName)).value();
            if (exists) {
                log.error("索引已存在：" + indexName);
                return;
            }

            createIndex(indexName, clazz);
        } catch (Exception e) {
            log.error("创建索引失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createIndex(String indexName, Class<?> clazz) throws IOException {
        Map<String, Property> properties = buildPropertiesFromClass(clazz);

        CreateIndexResponse response = client.indices().create(c -> c
                .index(indexName)
                .mappings(TypeMapping.of(m -> m.properties(properties)))
        );

        log.info(response.acknowledged() ? "索引创建成功：" + indexName : "索引创建失败");
    }

    // 提取 mapping 构建逻辑
    private Map<String, Property> buildPropertiesFromClass(Class<?> clazz) {
        Map<String, Property> properties = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            ESField esField = field.getAnnotation(ESField.class);
            if (esField == null) {
                continue;
            }

            String name = field.getName();
            String type = esField.type().getType();
            boolean isList = esField.isList();
            boolean isNested = esField.nested();
            boolean withKeyword = esField.keywordSubField();

            Class<?> fieldType = field.getType();
            Property property;

            // 处理集合类型字段（List<T>）
            if (isList) {
                if (isNested) {
                    // 嵌套集合
                    Map<String, Property> nestedProps = buildPropertiesFromClass(getGenericType(field));
                    property = Property.of(p -> p.nested(n -> n.properties(nestedProps)));
                } else {
                    // 普通集合（如 List<String>）
                    property = basicPropertyMapping(type, withKeyword);
                }
            } else {
                if (!isJavaBuiltinClass(fieldType) && type.equalsIgnoreCase("object")) {
                    Map<String, Property> objectProps = buildPropertiesFromClass(fieldType);
                    property = Property.of(p -> p.object(o -> o.properties(objectProps)));
                } else {
                    property = basicPropertyMapping(type, withKeyword);
                }
            }

            properties.put(name, property);
        }

        return properties;
    }

    // 基础字段的 property 映射
    private Property basicPropertyMapping(String type, boolean withKeyword) {
        return switch (type) {
            case "text" -> withKeyword ?
                    Property.of(p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k.ignoreAbove(256))))) :
                    Property.of(p -> p.text(t -> t));
            case "keyword" -> Property.of(p -> p.keyword(k -> k));
            case "integer" -> Property.of(p -> p.integer(i -> i));
            case "long" -> Property.of(p -> p.long_(l -> l));
            case "boolean" -> Property.of(p -> p.boolean_(b -> b));
            case "double" -> Property.of(p -> p.double_(d -> d));
            case "date" -> Property.of(p -> p.date(d -> d));
            default -> throw new IllegalArgumentException("不支持的字段类型: " + type);
        };
    }

    // 判断是否是 Java 内建类型
    private boolean isJavaBuiltinClass(Class<?> clazz) {
        return clazz.getPackageName().startsWith("java.") || clazz.isPrimitive();
    }

    private Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType pt) {
            Type actual = pt.getActualTypeArguments()[0];
            if (actual instanceof Class<?> clazz) {
                return clazz;
            }
        }
        throw new IllegalArgumentException("集合字段没有泛型类型：" + field.getName());
    }


}
