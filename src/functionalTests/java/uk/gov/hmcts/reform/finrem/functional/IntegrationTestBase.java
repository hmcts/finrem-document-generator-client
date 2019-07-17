package uk.gov.hmcts.reform.finrem.functional;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.finrem.functional.util.FunctionalTestUtils;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = uk.gov.hmcts.reform.finrem.functional.TestContextConfiguration.class)
public abstract class IntegrationTestBase {


    @Rule
    public SpringIntegrationMethodRule springIntegration;
    @Autowired
    protected FunctionalTestUtils utils;

    public static String serviceAuthUrl;


    public IntegrationTestBase() {
        this.springIntegration = new SpringIntegrationMethodRule();
    }

    @Autowired
    public void documentGeneratorServiceUrl(@Value("${document.generator.uri}")
                                                    String documentGeneratorServiceUrl) {
        RestAssured.baseURI = documentGeneratorServiceUrl;
    }

    @Autowired
    public void serviceAuthUrl(@Value("${idam.s2s-auth.url}")String serviceAuthUrl) {
        this.serviceAuthUrl = serviceAuthUrl;

    }

    public static void setServiceAuthUrlAsBaseUri() {
        RestAssured.baseURI = serviceAuthUrl;
    }

    @Autowired
    public static void setBulkPrintingUri(@Value("${bulk.print.uri}") String bulkPrintingUrl) {
        RestAssured.baseURI=bulkPrintingUrl;}

}
