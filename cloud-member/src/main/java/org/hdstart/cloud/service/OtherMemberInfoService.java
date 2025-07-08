package org.hdstart.cloud.service;

import org.hdstart.cloud.entity.OtherMemberInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.vo.OtherInfoVo;

/**
* @author 32600
* @description 针对表【other_member_info】的数据库操作Service
* @createDate 2025-06-03 11:52:05
*/
public interface OtherMemberInfoService extends IService<OtherMemberInfo> {

    Result updateOtherInfo(OtherInfoVo otherInfoVo);
}
