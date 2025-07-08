package org.hdstart.cloud.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.result.RE;
import org.hdstart.cloud.result.Result;
import org.hdstart.cloud.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 预处理方法，返回 false 则请求中断
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // 放行预检请求
        }

        log.info("请求路径:{}",request.getRequestURI());

        // 获取 token
        String token = request.getHeader("memberAuthorization");
        if (token != null) {
            // 校验逻辑
            try {
                Claims claims = JwtUtils.parseToken(token);
                Integer id = Integer.valueOf(claims.getSubject());
                String username = (String)claims.get("username");
                //TODO 查询数据库校验等其他逻辑
                Object storeToken = redisTemplate.opsForHash().get("tokens", id.toString());
                if (!token.equals(String.valueOf(storeToken))) {
                    responseWriteOtherLogin(response);
                    return false;
                }
                log.info("登录拦截放行");
                return true;
            } catch (Exception e) {
                responseWriteJsonTimeOut(response);
                log.error("有token但其他错误，不放行");
                return false;
            }
        }
        // 拦截响应
        responseWriteJson(response);
        log.error("无token，不放行");
        return false;
    }

    private void responseWriteJson (HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = new ObjectMapper().writeValueAsString(Result.error(RE.USER_NOT_LOGIN)); // 自己定义的统一返回格式
        response.getWriter().write(json);
    }

    private void responseWriteJsonTimeOut (HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = new ObjectMapper().writeValueAsString(Result.error(RE.USER_LOGIN_TIME_OUT)); // 自己定义的统一返回格式
        response.getWriter().write(json);
    }

    private void responseWriteOtherLogin (HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = new ObjectMapper().writeValueAsString(Result.error(RE.USER_LOGIN_OTHER_PLACE)); // 自己定义的统一返回格式
        response.getWriter().write(json);
    }
}
