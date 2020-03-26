package uk.gov.hmcts.reform.finrem.documentgenerator.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PdfGeneratorServiceHealthCheck extends AbstractServiceHealthCheck {

    @Autowired
    public PdfGeneratorServiceHealthCheck(
        @Value("${service.pdf-service.health.uri}/rs/status") String uri,
        @Qualifier("healthCheckRestTemplate") RestTemplate restTemplate) {
        super(uri, restTemplate);
    }
}
