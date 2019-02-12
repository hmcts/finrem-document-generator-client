package uk.gov.hmcts.reform.finrem.functional;

import feign.Feign;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;


@Slf4j
@Configuration
@ComponentScan("uk.gov.hmcts.reform.finrem")
@PropertySource(ignoreResourceNotFound = true, value = {"classpath:application-aat.properties"})
public class TestContextConfiguration {

//    @Bean
//    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(@Value("${service.auth.provider.base.url}") String s2sUrl,
//                                                               @Value("${s2s-auth.totp_secret}") String secret,
//                                                               @Value("${service.name}") String microservice) {
//        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
//            .encoder(new JacksonEncoder())
//            .contract(new SpringMvcContract())
//            .target(ServiceAuthorisationApi.class, s2sUrl);
//        log.info("S2S URL: {}", s2sUrl);
//        log.info("service.name: {}", microservice);
//        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
//    }
    @Bean
    public AuthTokenGenerator serviceAuthTokenGenerator(
        @Value("${idam.oauth2.client.secret}") final String secret,
        @Value("${idam.s2s-auth.microservice}") final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }

}
