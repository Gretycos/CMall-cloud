package com.tsong.cmall.goods.es.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Tsong
 * @Date 2023/7/18 16:07
 */
@Configuration
@ConfigurationProperties(prefix = "es")
@Data
public class ESClientConfig {
    private String host;
    private int port;

    @Bean
    public ElasticsearchClient client(){
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port)
        ).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
