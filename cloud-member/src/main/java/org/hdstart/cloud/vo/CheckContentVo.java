package org.hdstart.cloud.vo;

import lombok.Data;

import java.util.List;

@Data
public class CheckContentVo {

    private Integer id;
    private Integer memberId;
    private String textContent;
    private List<String> images;
}
