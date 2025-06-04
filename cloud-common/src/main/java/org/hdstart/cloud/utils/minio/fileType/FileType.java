package org.hdstart.cloud.utils.minio.fileType;

public enum FileType {

    AVATAR("avator"),
    NAS("nas"),
    BLOG("blog"),
    BACK_IMG("back-img"),;

    private String type;
    FileType (String type) {
        this.type = type;
    }

    public String getType () {
        return type;
    }
}
