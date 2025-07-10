package org.hdstart.cloud.vo;

import lombok.Data;

@Data
public class HistoryPointMessageVo {

    private Integer id;

    private Integer sendId;

    private Integer receiveId;

    private String message;

    private Integer isRead;

    private Integer status;
}
