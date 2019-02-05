package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.PDFGenerationException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class DocmosisPDFGenerationServiceTest {

    public static final String FILE_CONTENT = "Welcome to PDF document service";
    public static final ImmutableMap<String, Object> PLACEHOLDERS =
        ImmutableMap.of("caseDetails",caseDataMap());

    public static final String TEMPLATE_NAME = "template name";
    public static final String PDF_SERVICE_URI = "https://doc-gen/test";
    @Autowired
    private DocmosisPDFGenerationService pdfGenerationService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private static Map<String, Object> caseDataMap() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("PBANumber", "PBA123456");

        Map<String, Object> caseDataMap = new HashMap<>();
        caseDataMap.put("case_data", dataMap);

        return caseDataMap;
    }

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void generatePdfDocument() {
        mockServer.expect(requestTo(PDF_SERVICE_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(FILE_CONTENT, MediaType.APPLICATION_JSON));

        byte[] result = pdfGenerationService.generateDocFrom(TEMPLATE_NAME, PLACEHOLDERS);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(equalTo(FILE_CONTENT.getBytes())));
    }

    @Test
    public void generatePdfDocument400() {
        mockServer.expect(requestTo(PDF_SERVICE_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withBadRequest());

        try {
            pdfGenerationService.generateDocFrom(TEMPLATE_NAME, PLACEHOLDERS);
            fail("should have thrown bad-request exception");
        } catch (PDFGenerationException e) {
            HttpStatus httpStatus = ((HttpClientErrorException) e.getCause()).getStatusCode();
            assertThat(httpStatus, is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyTemplateName() {
        pdfGenerationService.generateDocFrom("", PLACEHOLDERS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullTemplateName() {
        pdfGenerationService.generateDocFrom(null, PLACEHOLDERS);
    }

    @Test(expected = NullPointerException.class)
    public void nullPlaceHoldersMap() {
        pdfGenerationService.generateDocFrom(TEMPLATE_NAME, null);
    }
}
