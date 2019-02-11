package uk.gov.hmcts.reform.finrem.functional.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.functional.SolCCDServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.functional.TestContextConfiguration;
import uk.gov.hmcts.reform.finrem.functional.idam.IdamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


import javax.annotation.PostConstruct;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
public class FunctionalTestUtils {

    @Value("${idam.api.url}")
    public String baseServiceOauth2Url = "";
    @Value("${idam_s2s_url}")
    public String idamS2sUrl;
    @Value("${user.id.url}")
    private String userId;
    @Value("${idam.username}")
    private String idamUserName;
    @Value("${idam.userpassword}")
    private String idamUserPassword;
    @Value("${idam.s2s-auth.microservice}")
    private String microservice;


    @Autowired
    private IdamUtils idamUtils;

    private String serviceToken;
    private String clientToken;


    @Autowired
    private AuthTokenGenerator authTokenGenerator;



    @PostConstruct
    public void init() {
        serviceToken = authTokenGenerator.generate();

    }


    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Headers getHeaders() {
        return getHeaders(clientToken);
    }

    public Headers getHeaders(String clientToken) {
        return Headers.headers(
            new Header("Authorization", clientToken),
            new Header("Content-Type", ContentType.JSON.toString()));
    }

    public Headers getHeadersWithUserId() {
        return getHeadersWithUserId(serviceToken, idamUserName);
    }


    private Headers getHeadersWithUserId(String serviceToken, String userId) {
        return Headers.headers(
            new Header("ServiceAuthorization", serviceToken),
            new Header("user-roles", "caseworker-divorce"),
            new Header("user-id", userId));
    }

    public String getUserId() {

        Claims claims = Jwts.parser()
            .parseClaimsJwt(idamUtils.generateUserTokenWithNoRoles(idamUserName, idamUserPassword)
                .substring(0, idamUtils.generateUserTokenWithNoRoles(idamUserName, idamUserPassword)
                    .lastIndexOf('.') + 1)).getBody();

        return claims.get("id", String.class);

    }

    public Headers getNewHeadersWithUserId() {
        return Headers.headers(
            new Header("ServiceAuthorization", "Bearer "
                + getServiceAuthToken("serviceAuth.json")),
            new Header("user-roles", "caseworker"),
            new Header("user-id", userId));
    }

    public Headers getNewHeaders() {

        return Headers.headers(
            new Header("Authorization", "Bearer "
                + idamUtils.generateUserTokenWithNoRoles(idamUserName, idamUserPassword)),
            new Header("Content-Type", ContentType.JSON.toString()));
    }

    public String getServiceAuthToken(String jsonFileName) {

        Response response = RestAssured.given()
            .relaxedHTTPSValidation()
            .contentType("application/json")
            .body(getJsonFromFile(jsonFileName))
            .post("http://rpe-service-auth-provider-aat.service.core-compute-aat.internal/testing-support/lease");
        String token1 = response.getBody().toString();

        return token1;
    }


    public String downloadPdfAndParseToString(String documentUrl) {
        Response document = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(getNewHeadersWithUserId())
            .when().get(documentUrl).andReturn();

        return parsePDFToString(document.getBody().asInputStream());
    }

    public String parsePDFToString(InputStream inputStream) {

        PDFParser parser;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;
        String parsedText = "";

        try {
            parser = new PDFParser(inputStream);
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Throwable t) {
            t.printStackTrace();


            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }

                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();


            }
            throw new Error(t);

        }

        return parsedText;
    }

}
