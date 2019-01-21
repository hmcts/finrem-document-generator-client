package uk.gov.hmcts.reform.finrem.documentgenerator.health;

public class EvidenceManagementServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://localhost:4006/health";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected AbstractServiceHealthCheck healthCheckInstance() {
        return new EvidenceManagementServiceHealthCheck(URI, restTemplate);
    }
}
