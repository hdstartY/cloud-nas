package org.hdstart.cloud.elasticsearch.constant;

public enum ESFieldType {
    TEXT("text"),
    KEYWORD("keyword"),
    INTEGER("integer"),
    LONG("long"),
    DOUBLE("double"),
    BOOLEAN("boolean"),
    OBJECT("object"),
    DATE("date");


    private String type;

    ESFieldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
