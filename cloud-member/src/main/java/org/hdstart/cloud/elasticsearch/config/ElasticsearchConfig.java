package org.hdstart.cloud.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig  {


    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 替换为你的 ES 地址和端口，支持 HTTPS 和认证
        RestClient restClient = RestClient.builder(
                new HttpHost("47.109.93.230", 9200, "http")).build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}
