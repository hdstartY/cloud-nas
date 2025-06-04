package org.hdstart.cloud.to;

import lombok.Data;

import java.util.List;

@Data
public class FollowedParamTo {

    private Integer currentPage;
    private Integer pageSize;
    private List<Integer> followedId;
    private String orderType;
}
