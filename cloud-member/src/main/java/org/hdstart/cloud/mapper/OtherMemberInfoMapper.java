package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.entity.OtherMemberInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 32600
* @description 针对表【other_member_info】的数据库操作Mapper
* @createDate 2025-06-03 11:52:05
* @Entity org.hdstart.cloud.entity.OtherMemberInfo
*/
public interface OtherMemberInfoMapper extends BaseMapper<OtherMemberInfo> {

    void insertOrUpdate(@Param("otherMemberInfo") OtherMemberInfo otherMemberInfo, @Param("memberId") Integer memberId);
}




