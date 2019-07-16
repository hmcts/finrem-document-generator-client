package uk.gov.hmcts.reform.finrem.documentgenerator.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceAuthProviderHealthCheck extends AbstractServiceHealthCheck {

    @Autowired
    public ServiceAuthProviderHealthCheck(
        @Value("${idam.s2s-auth.url}/health") String uri,
        RestTemplate restTemplate) {
        super(uri, restTemplate);
    }
}
