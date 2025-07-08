package org.hdstart.cloud.mapper;

import org.apache.ibatis.annotations.Param;
import org.hdstart.cloud.dto.MemberDTO;
import org.hdstart.cloud.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 32600
* @description 针对表【member】的数据库操作Mapper
* @createDate 2025-05-27 22:17:53
* @Entity org.hdstart.cloud.entity.Member
*/
public interface MemberMapper extends BaseMapper<Member> {

    MemberDTO getMemberInfo(@Param("id") Integer id);

    String getMemberNickName(@Param("id") Integer id);
}




