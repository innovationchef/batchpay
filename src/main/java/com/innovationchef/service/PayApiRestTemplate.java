package com.innovationchef.service;

import com.innovationchef.exception.ApiConfigException;
import com.innovationchef.props.PayApiConnProp;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
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

    private ClientHttpRequestFactory requestFactory() throws ApiConfigException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig())
                .setConnectionManager(connectionManager())
                .setKeepAliveStrategy(strategy());

        if (this.prop.isSSLEnabled()) {
            clientBuilder.setSSLContext(createSSLContext());
            clientBuilder.setSSLHostnameVerifier(new DefaultHostnameVerifier());
        }

        if (this.prop.isAuthEnabled()) {
            if (this.prop.getAuth().getAuthType().equalsIgnoreCase("basic")) {
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(this.prop.getAuth().getUsername(), this.prop.getAuth().getPassword()));
                clientBuilder.setDefaultCredentialsProvider(provider);
            }
        }

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

    // TODO: Yet to be tested
    private SSLContext createSSLContext() throws ApiConfigException {
        try {
            KeyStore keyStore = KeyStore.getInstance(this.prop.getSsl().getKeystoreType());
            InputStream kis = new FileInputStream(this.prop.getSsl().getKeystorePath());
            keyStore.load(kis, this.prop.getSsl().getKeystorePass().toCharArray());

            KeyStore trustStore = KeyStore.getInstance(this.prop.getSsl().getTruststoreType());
            InputStream tis = new FileInputStream(this.prop.getSsl().getTruststorePath());
            trustStore.load(tis, this.prop.getSsl().getTruststorePass().toCharArray());

            PrivateKeyStrategy aliasStrategy = (map, socket) -> this.prop.getSsl().getKeystoreKeyAlias();

            SSLContextBuilder builder = SSLContexts.custom();
            builder.loadKeyMaterial(keyStore, this.prop.getSsl().getKeystoreKeyPassword().toCharArray(), aliasStrategy);
            builder.loadTrustMaterial(trustStore, (cert, authType) -> true);

            return builder.build();
        } catch (KeyStoreException |
                IOException |
                CertificateException |
                NoSuchAlgorithmException |
                UnrecoverableKeyException |
                KeyManagementException e) {
            throw new ApiConfigException(e);
        }
    }
}
