package org.hdstart.cloud.config;

import org.hdstart.cloud.handler.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/follow/**",
                        "/blog/getBlogsByMemberId",
                        "/blog/publishBlog",
                        "/blog/storeLike",
                        "/pointMessage/getNotRedMessages",
                        "/pointMessage/getHistory",
                        "/blog/listRecoverBlogs",
                        "/comment/save",
                        "/leave/leaveMessage") // 拦截路径
                .excludePathPatterns(
                        "/member/loginByPhone",
                        "/member/loginByEmail",
                        "/public/**"
                        ); // 放行路径
    }
}