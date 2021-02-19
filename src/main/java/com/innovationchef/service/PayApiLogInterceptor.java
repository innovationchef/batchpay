package com.innovationchef.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Log4j2
public class PayApiLogInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        traceRequest(httpRequest, bytes);
        Instant start = Instant.now();
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        Instant end = Instant.now();
        log.info("Total time taken: {}ms", Duration.between(start, end).toMillis());
        traceResponse(response);
        return response;
    }

    private void traceRequest(final HttpRequest request, final byte[] body) throws IOException {
        log.info("========================== REQUEST BEGIN ==========================");
        log.info("URI:                    : {}", request.getURI());
        log.info("Method:                 : {}", request.getMethod());
        log.info("Headers:                : {}", request.getHeaders());
        log.info("Request Body:           : {}", body);
        log.info("=========================== REQUEST END ===========================");
    }

    private void traceResponse(final ClientHttpResponse response) throws IOException {
        InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
        String line = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
        log.info("========================== RESPONSE BEGIN ==========================");
        log.info("Status Code:            : {}", response.getRawStatusCode());
        log.info("Headers:                : {}", response.getHeaders());
        log.info("Response Body:           : {}", line);
        log.info("=========================== REQUEST END ===========================");
    }
}
