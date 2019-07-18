package uk.gov.hmcts.reform.finrem.functional.documents;


import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.finrem.functional.IntegrationTestBase;
import uk.gov.hmcts.reform.finrem.functional.idam.IdamUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SerenityRunner.class)
public class FinancialRemedyDocumentGeneratorTests extends IntegrationTestBase {

    private static String SOLICITOR_FIRM = "Michael Jones & Partners";
    private static String SOLICITOR_NAME = "Jane Smith";
    private static String APPLICANT_NAME = "Williams";
    private static String DIVORCE_CASENO = "DD12D12345";
    private static String SOLICITOR_REF = "JAW052018";
    private static String BULKPRINT_URL = "/bulk-print";
    private static String errMsg;

    @Value("${bulk.print.uri}")
    private String bulkprintUrl;

    @Value("${idam.s2s-auth.microservice}")
    private String microservice;

    @Value("${document.management.store.baseUrl}")
    private String dmStoreBaseUrl;

    @Autowired
    private IdamUtils idamUtils;

    @After
    public void tearDown() {
        utils.deleteIdamUser();
    }

    @Test
    public void verifyDocumentGenerationShouldReturnOkResponseCode() {
        validatePostSuccess("documentGeneratePayload.json");
    }

    @Test
    public void verifyBulkPrintingisSuccessful() {

        validateBulkPrintSuccess("bulkprinting.json", bulkprintUrl);

    }


    @Test
    public void verifyDocumentGenerationPostResponseContent() {
        Response response = generateDocument("documentGeneratePayload.json");
        System.out.println("response is : " + response.prettyPrint());
        JsonPath jsonPathEvaluator = response.jsonPath();
        assertTrue(jsonPathEvaluator.get("fileName").toString().equalsIgnoreCase("OnlineFormA.pdf"));
        assertTrue(jsonPathEvaluator.get("mimeType").toString().equalsIgnoreCase("application/pdf"));
    }

    @Test
    public void verifyGeneratedDocumentCanBeAccessedAndVerifyGetResponseContent() {
        Response response = generateDocument("documentGeneratePayload.json");
        JsonPath jsonPathEvaluator = response.jsonPath();
        String url = jsonPathEvaluator.get("url");
        validatePostSuccessForaccessingGeneratedDocument(fileRetrieveUrl(url));
        validatePostSuccessForaccessingGeneratedDocument(fileRetrieveUrl(url));
        Response response1 = accessGeneratedDocument(fileRetrieveUrl(url));
        JsonPath jsonPathEvaluator1 = response1.jsonPath();
        assertTrue(jsonPathEvaluator1.get("originalDocumentName").toString().equalsIgnoreCase("OnlineFormA.pdf"));
        assertTrue(jsonPathEvaluator1.get("mimeType").toString().equalsIgnoreCase("application/pdf"));
        assertTrue(jsonPathEvaluator1.get("classification").toString().equalsIgnoreCase("RESTRICTED"));
    }

    @Test
    public void downloadDocumentAndVerifyContentAgainstOriginalJsonFileInput() {
        Response response = generateDocument("documentGeneratePayload.json");
        JsonPath jsonPathEvaluator = response.jsonPath();
        String documentUrl = jsonPathEvaluator.get("url") + "/binary";
        String documentContent = utils.downloadPdfAndParseToString(fileRetrieveUrl(documentUrl));
        assertTrue(documentContent.contains(SOLICITOR_FIRM));
        assertTrue(documentContent.contains(SOLICITOR_NAME));
        assertTrue(documentContent.contains(APPLICANT_NAME));
        assertTrue(documentContent.contains(DIVORCE_CASENO));
        assertTrue(documentContent.contains(SOLICITOR_REF));

    }

    private void validatePostSuccess(String jsonFileName) {
        SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(idamUtils.getHeaders())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post()
            .then()
            .assertThat().statusCode(200);
    }

    private void validateBulkPrintSuccess(String jsonFileName,String url) {
        setBulkPrintingUri(url);
        System.out.println("url is " + url);
        Response response = SerenityRest.given()
            .relaxedHTTPSValidation()
            .header("Content-Type", ContentType.JSON.toString())
            .body(utils.getJsonFromFile(jsonFileName))
            .and().post( );
        errMsg = response.prettyPrint();
        System.out.println("response is " + response.prettyPrint());
        assertEquals(errMsg,200,response.getStatusCode());


    }

    private Response generateDocument(String jsonFileName) {

        Response jsonResponse = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(idamUtils.getHeaders())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post().andReturn();

        return jsonResponse;
    }

    private void validatePostSuccessForaccessingGeneratedDocument(String url) {
        SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(idamUtils.getHeadersWithUserId())
            .when().get(url)
            .then().assertThat().statusCode(200);

    }

    private Response accessGeneratedDocument(String url) {
        Response jsonResponse = SerenityRest.given()
            .relaxedHTTPSValidation()
            .headers(idamUtils.getHeadersWithUserId())
            .when().get(url)
            .andReturn();
        return jsonResponse;
    }

    private String fileRetrieveUrl(String url) {
        if (url != null && url.contains("document-management-store:8080")) {
            return url.replace("http://document-management-store:8080", dmStoreBaseUrl);
        }

        return url;
    }
}




