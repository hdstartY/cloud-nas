package org.hdstart.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import org.hdstart.cloud.entity.Admin;
import org.hdstart.cloud.result.RE;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.service.AdminService;
import org.hdstart.cloud.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("login")
    public Result<Map> login (@RequestBody Admin admin) {
        if (admin.getPhone() != null) {
            Admin storeAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("phone", admin.getPhone()));
            if (storeAdmin != null && storeAdmin.getPassword().equals(admin.getPassword())) {
                String token = JwtUtils.generateToken(String.valueOf(storeAdmin.hashCode()), storeAdmin.getPhone());
                HashMap<String, String> returnToken = new HashMap<>();
                returnToken.put("token", token);
                return Result.success(returnToken);
            }
        }

        if (admin.getEmail() != null) {
            Admin storeAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("email", admin.getEmail()));
            if (storeAdmin != null && storeAdmin.getPassword().equals(admin.getPassword())) {
                String token = JwtUtils.generateToken(storeAdmin.getId().toString(), storeAdmin.getPhone());
                HashMap<String, String> returnToken = new HashMap<>();
                returnToken.put("token", token);
                return Result.success(returnToken);
            }
        }

        return Result.error(RE.USER_NOT_FOUND);
    }

    @PostMapping("register")
    public Result<Map> register (@Valid @RequestBody Admin admin) {
        adminService.save(admin);
        HashMap<String, String> message = new HashMap<>();
        message.put("msg","注册成功");
        return Result.success(message);
    }
}
