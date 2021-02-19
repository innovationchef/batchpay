package com.innovationchef.service;

import com.innovationchef.props.PayApiConnProp;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class PayApiRestTemplate extends RestTemplate implements InitializingBean {

    private final PayApiConnProp prop;

    public PayApiRestTemplate(PayApiConnProp prop) {
        super();
        this.prop = prop;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setRequestFactory(requestFactory());
        this.setErrorHandler(new PayApiErrorHandler());
        this.setInterceptors(interceptors());
    }

    private List<ClientHttpRequestInterceptor> interceptors() {
        List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = new ArrayList<>();
        clientHttpRequestInterceptors.add(new PayApiHeaderInterceptor());
        clientHttpRequestInterceptors.add(new PayApiLogInterceptor());
        return clientHttpRequestInterceptors;
    }

    private ClientHttpRequestFactory requestFactory() {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig())
                .setConnectionManager(connectionManager())
                .setKeepAliveStrategy(strategy());
        CloseableHttpClient client = clientBuilder.build();
        return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
    }

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(this.prop.getTimeout())
                .setConnectionRequestTimeout(this.prop.getRequestTimeout())
                .setSocketTimeout(this.prop.getSocketTimeout())
                .build();
    }

    private PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(this.prop.getMaxTotal());
        manager.setDefaultMaxPerRoute(this.prop.getMaxPerRoute());
        return manager;
    }

    private ConnectionKeepAliveStrategy strategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement element = it.nextElement();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return this.prop.getKeepAlive() * 1000;
        };
    }
}
