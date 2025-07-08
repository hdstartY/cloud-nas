package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.MemberFollow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 32600
* @description 针对表【member_follow】的数据库操作Mapper
* @createDate 2025-06-01 12:29:00
* @Entity org.hdstart.cloud.entity.MemberFollow
*/
public interface MemberFollowMapper extends BaseMapper<MemberFollow> {

    List<Integer> getFollowedMembersIds(@Param("followerId") Integer followerId);
}




