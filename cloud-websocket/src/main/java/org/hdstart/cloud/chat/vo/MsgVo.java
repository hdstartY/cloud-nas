package org.hdstart.cloud.chat.vo;

import lombok.Data;

@Data
public class MsgVo {

    private Integer sendId;

    private String sendNickName;

    private Integer receiveId;

    private Integer status;

    private String message;
}
