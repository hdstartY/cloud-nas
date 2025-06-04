package org.hdstart.cloud.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public static<T> Result<T> success(T data) {
        Result<T> success = build(RE.SUCCESS.getCode(),RE.SUCCESS.getMsg(), data);
        return success;
    }

    public static<T> Result<Map<String, T>> success(String key,T value) {
        Map<String,T> data = new HashMap<>();
        data.put(key,value);
        Result<Map<String, T>> result = build(RE.SUCCESS.getCode(), RE.SUCCESS.getMsg(), data);
        return result;
    }

    public static<T> Result<T> error(RE error) {
        Result<T> result = build(error.getCode(), error.getMsg(), null);
        return result;
    }

    public static<T> Result<Map<String, T>> error(String key,T value) {
        Map<String,T> data = new HashMap<>();
        data.put(key,value);
        Result<Map<String, T>> result = build(RE.ERROR.getCode(), RE.ERROR.getMsg(), data);
        return result;
    }

    public static<T> Result<T> build(Integer code, String msg,T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static<T> Result<T> build(RE re,T data) {
        Result<T> result = new Result<>();
        result.setCode(re.getCode());
        result.setMsg(re.getMsg());
        result.setData(data);
        return result;
    }
}
