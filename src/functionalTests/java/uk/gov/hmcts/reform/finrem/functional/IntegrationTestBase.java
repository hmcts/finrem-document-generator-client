package uk.gov.hmcts.reform.finrem.functional;

import io.restassured.RestAssured;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.finrem.functional.util.FunctionalTestUtils;
import uk.gov.hmcts.reform.finrem.functional.util.RegisteredUserDao;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = uk.gov.hmcts.reform.finrem.functional.TestContextConfiguration.class)
@PropertySource("application.properties")
public abstract class IntegrationTestBase {


    @Autowired
    protected FunctionalTestUtils utils;

    @Autowired
    protected RegisteredUserDao registeredUserDao;

    private String documentGeneratorServiceUrl;
    public static String documentURL;
    public static String authorizationTokenURL;
    public static String serviceAuthorizationTokenURL;
    public static String accountCreationURL;


    @Autowired
    public void documentGeneratorServiceUrl(@Value("http://localhost:4009/version/1/generatePDF") String documentGeneratorServiceUrl) {
        this.documentGeneratorServiceUrl = documentGeneratorServiceUrl;
        RestAssured.baseURI = documentGeneratorServiceUrl;
    }

    @Autowired
    public void documentURL(@Value("http://localhost:3405") String documentURL) {
        this.documentURL = documentURL;

    }

    @Autowired
    public void authorizationTokenURL(@Value("http://localhost:4501/testing-support/lease") String authorizationTokenURL) {
        this.authorizationTokenURL = authorizationTokenURL;

    }

    @Autowired
    public void ServiceAuthorizationTokenURL(@Value("http://localhost:4502/testing-support/lease") String serviceAuthorizationTokenURL)
    {
        this.serviceAuthorizationTokenURL=serviceAuthorizationTokenURL;
    }

    @Autowired
    public void accountCreationURL(@Value("http://localhost:4501/testing-support/accounts") String accountCreationURL)
    {
        this.accountCreationURL=accountCreationURL;
    }

    public static void setDocumentURLAsBaseUri() {
        RestAssured.baseURI = documentURL;
    }

    public static void setauthorizationTokenURLAsBaseUri() { RestAssured.baseURI = authorizationTokenURL; }

    public static void setserviceAuthorizationTokenURLAsBaseUri() { RestAssured.baseURI = serviceAuthorizationTokenURL; }

    public static void setAccountCreationURLAsBaseUri() { RestAssured.baseURI = accountCreationURL; }



    @Rule
    public SpringIntegrationMethodRule springIntegration;

    public IntegrationTestBase() {
        this.springIntegration = new SpringIntegrationMethodRule();

    }
}
