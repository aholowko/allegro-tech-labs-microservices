package pl.allegro.atl;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, MeterRegistry meterRegistry) {
        final PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(10);
        manager.setDefaultMaxPerRoute(10);

        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(50)
                .setSocketTimeout(200)
                .build();

        meterRegistry.gauge(
                "httpClientLeased", manager,
                m -> m.getTotalStats().getLeased()
        );
        meterRegistry.gauge(
                "httpClientMax", manager,
                m -> m.getTotalStats().getMax()
        );
        meterRegistry.gauge(
                "httpClientUtilization", manager,
                m -> {
                    final PoolStats totalStats = m.getTotalStats();
                    final int leased = totalStats.getLeased();
                    final int max = totalStats.getMax();
                    return (double) leased / (double) max;
                }
        );

        final HttpClientBuilder clientBuilder = HttpClients.custom()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(config);
        final HttpClient httpClient = clientBuilder.build();
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return restTemplateBuilder
                .requestFactory(() -> requestFactory)
                .build();
    }
}
