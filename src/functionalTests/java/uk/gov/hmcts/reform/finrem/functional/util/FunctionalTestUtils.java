package uk.gov.hmcts.reform.finrem.functional.util;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.finrem.functional.IntegrationTestBase;
import java.util.*;

import uk.gov.hmcts.reform.finrem.functional.TestContextConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
public class FunctionalTestUtils {


//    @Value("${user.id.url}")
    private String userId=null;
    private String email=null;
    private String apiBody;
    private Headers headers;

    @Autowired
    protected RegisteredUserDao registeredUserDao;


    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Headers getHeadersWithToken()
    {
        userIdAndEmail();
        IntegrationTestBase.setauthorizationTokenURLAsBaseUri();
        Response response= SerenityRest.given()
            .queryParam("role","caseworker-divorce")
            .queryParam("id",userId)
            .when().post(IntegrationTestBase.authorizationTokenURL)
            .then()
            .extract()
            .response();
        return getHeaders(response.getBody().asString(),"user");

    }

    public Headers getHeadersWithUserAndServiceToken()
    {
        userIdAndEmail();
        IntegrationTestBase.setserviceAuthorizationTokenURLAsBaseUri();
        apiBody = "{\"microservice\":\"finrem_document_generator\"}";
        Response response= SerenityRest.
                            given().
                            header("Content-type","application/json")
                            .body(apiBody)
                            .when()
                            .post()
                            .then()
                            .extract()
                            .response();
        return getHeaders(response.getBody().asString(),"service");


    }

    public Headers getHeaders(String JsonBody , String Token)
    {
        switch(Token) {
            case "user":
                headers = Headers.headers(
                    new Header("Authorization", "Bearer " + JsonBody),
                    new Header("Content-Type", ContentType.JSON.toString()));
                break;
            case "service":
                headers = Headers.headers(
                    new Header("ServiceAuthorization", "Bearer " + JsonBody),
                    new Header("user-id", email),
                    new Header("user-roles", "caseworker-divorce"));
                break;
        }
                return headers;

    }

    private void userIdAndEmail()
    {
        Map<String,String> user = registeredUserDao.getUserDetails();
        if (user.size()== 0)
        {
            createCaseWorkerUser();
        }
         userId = user.get("id");
         email = user.get("email");
    }

    private void createCaseWorkerUser()
    {
        IntegrationTestBase.setAccountCreationURLAsBaseUri();
             SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers("Content-Type", ContentType.JSON.toString())
            .body(getJsonFromFile("userCreation.json"))
            .when().post().andReturn();

    }

    public String downloadPdfAndParseToString(String documentUrl) {
        Response document = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(getHeadersWithUserAndServiceToken())
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
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1) {
                e.printStackTrace();
            }

        }
        return parsedText;
    }
}
