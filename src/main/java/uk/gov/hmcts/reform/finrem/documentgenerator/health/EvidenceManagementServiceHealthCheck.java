package uk.gov.hmcts.reform.finrem.documentgenerator.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EvidenceManagementServiceHealthCheck extends AbstractServiceHealthCheck {

    @Autowired
    public EvidenceManagementServiceHealthCheck(
        @Value("${service.evidence-management-client-api.health.uri}") String uri,
        RestTemplate restTemplate) {
        super(uri, restTemplate);
    }
}
