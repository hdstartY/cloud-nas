package org.hdstart.cloud.result;

public enum RE {

    SUCCESS(200,"success"),
    ERROR(500,"error"),
    USER_NOT_FOUND(401,"没有此用户"),
    USER_NOT_LOGIN(401,"用户未登录"),
    TEACHER_ADD_FAILD(401,"用户添加失败"),
    USER_REGISTER_FAILD(401,"用户注册失败,该用户已存在"),
    USER_PHONE_EMPTY(401,"登录手机号不能为空"),
    USER_EMAIL_EMPTY(401,"登录邮箱不能为空"),
    USER_PASSWORD_ERROR(401,"密码错误"),
    USER_LOGIN_SUCCESS(200,"登陆成功");

    Integer code;
    String msg;

    RE(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
