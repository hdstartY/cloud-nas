package org.hdstart.cloud.utils.minio.fileType;

public enum FileType {

    AVATAR("avator"),
    NAS("nas"),
    BLOG("blog"),
    MSG_IMG("msg-img"),
    PRE_IMG("pre-img"),
    VIDEO_PRE("video_pre"),
    VIDEO("video"),
    BACK_IMG("back-img");

    private String type;
    FileType (String type) {
        this.type = type;
    }

    public String getType () {
        return type;
    }
}
