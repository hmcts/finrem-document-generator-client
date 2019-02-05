package uk.gov.hmcts.reform.finrem.documentgenerator.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PDFGeneratorServiceHealthCheck extends AbstractServiceHealthCheck {

    @Autowired
    public PDFGeneratorServiceHealthCheck(
        @Value("${service.pdf-service.health.uri}") String uri,
        RestTemplate restTemplate) {
        super(uri, restTemplate);
    }
}
