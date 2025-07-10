package org.hdstart.cloud.utils.minio.fileType;

public enum FileType {

    AVATAR("avator"),
    NAS("nas"),
    BLOG("blog"),
    MSG_IMG("msg-img"),
    BACK_IMG("back-img");

    private String type;
    FileType (String type) {
        this.type = type;
    }

    public String getType () {
        return type;
    }
}
