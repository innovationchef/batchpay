package com.innovationchef.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConstructorBinding
@ConfigurationProperties("batchpay.payapi.conn")
public class PayApiConnProp {

    /**
     * Max total connections
     */
    private final int maxTotal;

    /**
     * Max total connections per route
     */
    private final int maxPerRoute;

    /**
     * Connection timeout
     */
    private final int timeout;

    /**
     * Request timeout
     */
    private final int requestTimeout;

    /**
     * Socket timeout
     */
    private final int socketTimeout;

    /**
     * Keep-alive time
     */
    private final int keepAlive;

    /**
     * Are endpoints TLS/SSL enabled
     */
    private final boolean isSSLEnabled;

    /**
     * Configurations for SSL/TLS
     */
    private final SSLProp ssl;

    /**
     * Configurations for retries
     */
    private final Retry retry;

    public PayApiConnProp(@DefaultValue("30") int maxTotal,
                          @DefaultValue("30") int maxPerRoute,
                          @DefaultValue("30") int timeout,
                          @DefaultValue("30") int requestTimeout,
                          @DefaultValue("30") int socketTimeout,
                          @DefaultValue("30") int keepAlive,
                          @DefaultValue("false") boolean isSSLEnabled,
                          SSLProp ssl,
                          Retry retry) {
        this.maxTotal = maxTotal;
        this.maxPerRoute = maxPerRoute;
        this.timeout = timeout;
        this.requestTimeout = requestTimeout;
        this.socketTimeout = socketTimeout;
        this.keepAlive = keepAlive;
        this.isSSLEnabled = isSSLEnabled;
        this.ssl = ssl;
        this.retry = retry;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public boolean isSSLEnabled() {
        return isSSLEnabled;
    }

    public SSLProp getSsl() {
        return ssl;
    }

    public Retry getRetry() {
        return retry;
    }

    public static class Retry {

        /**
         * Max retry attempts
         */
        private final int maxAttempts;

        /**
         * backoff delay - min
         */
        private final int minBackoff;

        /**
         * Backoff delay - max
         */
        private final int maxBackoff;

        @ConstructorBinding
        public Retry(@DefaultValue("3") int maxAttempts,
                     @DefaultValue("1000") int minBackoff,
                     @DefaultValue("4000") int maxBackoff) {
            this.maxAttempts = maxAttempts;
            this.minBackoff = minBackoff;
            this.maxBackoff = maxBackoff;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public int getMinBackoff() {
            return minBackoff;
        }

        public int getMaxBackoff() {
            return maxBackoff;
        }
    }

    public static class SSLProp {

        /**
         * Keystore type - JKS or PKCS12
         */
        private final String keystoreType;

        /**
         * Keystore path
         */
        private final String keystorePath;

        /**
         * Keystore password
         */
        private final String keystorePass;

        /**
         * Keystore key alias
         */
        private final String keystoreKeyAlias;

        /**
         * Keystore key password
         */
        private final String keystoreKeyPassword;

        /**
         * Truststore type - JKS
         */
        private final String truststoreType;

        /**
         * Truststore path
         */
        private final String truststorePath;

        /**
         * Truststore password
         */
        private final String truststorePass;

        @ConstructorBinding
        public SSLProp(String keystoreType,
                       String keystorePath,
                       String keystorePass,
                       String keystoreKeyAlias,
                       String keystoreKeyPassword,
                       String truststoreType,
                       String truststorePath,
                       String truststorePass) {
            this.keystoreType = keystoreType;
            this.keystorePath = keystorePath;
            this.keystorePass = keystorePass;
            this.keystoreKeyAlias = keystoreKeyAlias;
            this.keystoreKeyPassword = keystoreKeyPassword;
            this.truststoreType = truststoreType;
            this.truststorePath = truststorePath;
            this.truststorePass = truststorePass;
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public String getKeystorePath() {
            return keystorePath;
        }

        public String getKeystorePass() {
            return keystorePass;
        }

        public String getKeystoreKeyAlias() {
            return keystoreKeyAlias;
        }

        public String getKeystoreKeyPassword() {
            return keystoreKeyPassword;
        }

        public String getTruststoreType() {
            return truststoreType;
        }

        public String getTruststorePath() {
            return truststorePath;
        }

        public String getTruststorePass() {
            return truststorePass;
        }
    }
}
