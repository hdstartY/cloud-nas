package org.hdstart.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hdstart.cloud.entity.Admin;
import org.hdstart.cloud.service.AdminService;
import org.hdstart.cloud.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author 32600
* @description 针对表【admin】的数据库操作Service实现
* @createDate 2025-05-25 20:28:23
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




