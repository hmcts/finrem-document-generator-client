package uk.gov.hmcts.reform.finrem.documentgenerator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.logging.httpcomponents.OutboundRequestIdSettingInterceptor;
import uk.gov.hmcts.reform.logging.httpcomponents.OutboundRequestLoggingInterceptor;

import static java.util.Arrays.asList;

@Configuration
public class HttpConnectionConfiguration {

    @Value("${http.connect.timeout}")
    private int httpConnectTimeout;

    @Value("${http.connect.request.timeout}")
    private int httpConnectRequestTimeout;

    @Value("${health-check.http.connect.timeout}")
    private int healthCheckHttpConnectTimeout;

    @Value("${health-check.http.connect.request.timeout}")
    private int healthCheckHttpConnectRequestTimeout;


    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter jackson2HttpCoverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter jackson2HttpConverter
            = new MappingJackson2HttpMessageConverter(objectMapper);
        jackson2HttpConverter.setSupportedMediaTypes(ImmutableList.of(MediaType.APPLICATION_JSON));
        return jackson2HttpConverter;
    }

    @Bean
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter jackson2HttpMessageConverter) {
        RestTemplate restTemplate = new RestTemplate(asList(
            jackson2HttpMessageConverter,
            new ByteArrayHttpMessageConverter(),
            new FormHttpMessageConverter(),
            new ResourceHttpMessageConverter()
        ));
        restTemplate.setRequestFactory(getClientHttpRequestFactory(httpConnectTimeout, httpConnectRequestTimeout));

        return restTemplate;
    }

    @Bean
    public RestTemplate healthCheckRestTemplate(MappingJackson2HttpMessageConverter jackson2HttpMessageConverter) {
        RestTemplate restTemplate = new RestTemplate(asList(
            jackson2HttpMessageConverter,
            new ByteArrayHttpMessageConverter(),
            new FormHttpMessageConverter(),
            new ResourceHttpMessageConverter()
        ));
        restTemplate.setRequestFactory(getClientHttpRequestFactory(healthCheckHttpConnectTimeout, healthCheckHttpConnectRequestTimeout));

        return restTemplate;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(int httpConnectTimeout, int httpConnectRequestTimeout) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(httpConnectTimeout)
                .setConnectionRequestTimeout(httpConnectRequestTimeout)
                .build();

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .useSystemProperties()
                .addInterceptorFirst(new OutboundRequestIdSettingInterceptor())
                .addInterceptorFirst((HttpRequestInterceptor) new OutboundRequestLoggingInterceptor())
                .addInterceptorLast((HttpResponseInterceptor) new OutboundRequestLoggingInterceptor())
                .setDefaultRequestConfig(config)
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
