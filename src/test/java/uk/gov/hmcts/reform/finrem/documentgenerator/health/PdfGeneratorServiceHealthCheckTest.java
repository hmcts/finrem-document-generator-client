package uk.gov.hmcts.reform.finrem.documentgenerator.health;

public class PdfGeneratorServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://localhost:4006/status";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected PdfGeneratorServiceHealthCheck healthCheckInstance() {
        return new PdfGeneratorServiceHealthCheck(URI, restTemplate);
    }
}
