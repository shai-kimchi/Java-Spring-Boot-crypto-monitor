package com.lian.shai.currencymonitor.configuration;

import com.lian.shai.currencymonitor.data.ElasticsearchClient;
import com.lian.shai.currencymonitor.data.ElasticsearchClientImpl;
import com.lian.shai.currencymonitor.data.IpProvider;
import com.lian.shai.currencymonitor.data.WebCrawler;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Configuration
public class SpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpringConfiguration.class);

    @Value("${elasticsearch.url}")
    private String elasticSearchServerUrl;

    @Bean
    public WebCrawler crawler() {
        return new WebCrawler();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClientImpl();
    }

    @Bean
    public IpProvider ipProvider() { return new IpProvider(); }

    @Bean
    public RestHighLevelClient elasticClient() throws MalformedURLException {

        URL url = new URL(elasticSearchServerUrl);

        logger.info("ElasticSearch URL: {}", elasticSearchServerUrl);

        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(host,port, protocol));

        return new RestHighLevelClient(builder);
    }

    @Bean
    public ConfigSuspectWords configSuspectWords() throws IOException, URISyntaxException {
        return new ConfigSuspectWords();
    }
}
