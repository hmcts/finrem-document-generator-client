package uk.gov.hmcts.reform.finrem.documentgenerator.health;

public class PDFGeneratorServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://localhost:4006/status";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected PDFGeneratorServiceHealthCheck healthCheckInstance() {
        return new PDFGeneratorServiceHealthCheck(URI, restTemplate);
    }
}
