package com.innovationchef.service;

import com.innovationchef.exception.RetryableApiException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Log4j2
public class PayApiErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        int rawStatusCode = clientHttpResponse.getRawStatusCode();
        if (rawStatusCode >= 300 || rawStatusCode < 200)
            return true;
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[Error Code: ")
                .append(clientHttpResponse.getRawStatusCode())
                .append("] - ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = reader.readLine();
            }
        } catch (NullPointerException e) {
            log.warn("No response body received");
        }
        throw new RetryableApiException(sb.toString());
    }
}
