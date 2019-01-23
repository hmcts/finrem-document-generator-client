package uk.gov.hmcts.reform.finrem.functional.documents;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.finrem.functional.IntegrationTestBase;
import static org.junit.Assert.assertTrue;
import java.sql.*;

@RunWith(SerenityRunner.class)
public class FinancialRemedyDocumentGeneratorTests1 extends IntegrationTestBase
{
    private static final String SOLICITOR_FIRM = "Michael Jones & Partners";

    @Test
    public void verifyDocumentGenerationShouldReturnOkResponseCode() {

        validatePostSuccess("documentGeneratePayload.json");
    }

    @Test
    public void verifyDocumentGenerationResponse()
    {
        Response response= generateDocument("documentGeneratePayload.json");
        JsonPath jsonPathEvaluator = response.jsonPath();
        assertTrue(jsonPathEvaluator.get("fileName").toString().equalsIgnoreCase("MiniFormA.pdf"));
       assertTrue(jsonPathEvaluator.get("mimeType").toString().equalsIgnoreCase("application/pdf"));
    }

   @Test
    public void verifyGeneratedDocumentCanBeAccessedAndVerifyResponse()
    {
        Response response= generateDocument("documentGeneratePayload.json");
        JsonPath jsonPathEvaluator = response.jsonPath();
        String url = jsonPathEvaluator.get("url");
        validatePostSuccessForaccessingGeneratedDocument(url.replaceAll("document-management-store:8080","localhost:3405"));
        Response response1=accessGeneratedDocument(url.replaceAll("document-management-store:8080","localhost:3405"));
        JsonPath jsonPathEvaluator1 = response1.jsonPath();
        assertTrue(jsonPathEvaluator1.get("originalDocumentName").toString().equalsIgnoreCase("MiniFormA.pdf"));
        assertTrue(jsonPathEvaluator1.get("mimeType").toString().equalsIgnoreCase("application/pdf"));
        assertTrue(jsonPathEvaluator1.get("classification").toString().equalsIgnoreCase("RESTRICTED"));
    }

    @Test
    public void downloadDocumentAndVerifyContent()
    {
        Response response= generateDocument("documentGeneratePayload.json");
        JsonPath jsonPathEvaluator = response.jsonPath();
        String documentUrl = jsonPathEvaluator.get("url")+"/binary";
        String url= documentUrl.replaceAll("document-management-store:8080","localhost:3405");
        String documentContent= utils.downloadPdfAndParseToString(url);
        assertTrue(documentContent.contains(SOLICITOR_FIRM));

    }

    private void validatePostSuccess(String jsonFileName) {
        SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithToken())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post()
            .then().assertThat().statusCode(200);
    }

    private Response generateDocument(String jsonFileName) {

        Response jsonResponse = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithToken())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post().andReturn();
        return jsonResponse;
    }

    private void validatePostSuccessForaccessingGeneratedDocument(String url)
    {
        SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserAndServiceToken())
            .when().get(url)
            .then().assertThat().statusCode(200);

    }

    private Response accessGeneratedDocument(String url)
    {
        Response jsonResponse = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserAndServiceToken())
            .when().get(url)
            .andReturn();
        return jsonResponse;
    }

}




