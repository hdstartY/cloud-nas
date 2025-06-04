package org.hdstart.cloud.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveMessageVo {

    private Integer id;

    private Integer memberId;

    private String messageNickName;

    private String avatar;

    private String textContent;

    private LocalDateTime createTime;
}
