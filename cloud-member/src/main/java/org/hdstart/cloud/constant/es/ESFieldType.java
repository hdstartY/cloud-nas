package org.hdstart.cloud.constant.es;

public enum ESFieldType {
    TEXT("text"),
    KEYWORD("keyword"),
    INTEGER("integer"),
    LONG("long"),
    DOUBLE("double"),
    BOOLEAN("boolean"),
    DATE("date");


    private String type;

    ESFieldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
