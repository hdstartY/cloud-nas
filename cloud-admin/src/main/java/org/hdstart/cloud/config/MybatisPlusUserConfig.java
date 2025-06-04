package org.hdstart.cloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.hdstart.cloud.mapper")
public class MybatisPlusUserConfig {

}
