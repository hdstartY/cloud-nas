package org.hdstart.cloud.handler;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.hdstart.cloud.threadlocal.FeignTokenThreadLocal;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String token = FeignTokenThreadLocal.getToken();

        if (token != null) {
            template.header("Authorization", token);
        }
    }
}
