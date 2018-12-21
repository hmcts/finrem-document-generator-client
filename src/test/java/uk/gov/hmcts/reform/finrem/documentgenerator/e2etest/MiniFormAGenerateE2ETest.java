package uk.gov.hmcts.reform.finrem.documentgenerator.e2etest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.documentResponse;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.documentStoreServiceResponse;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.invalidRequest;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.pdfServiceResponse;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.templateNotSuppliedRequest;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.templateValuesNotSuppliedRequest;
import static uk.gov.hmcts.reform.finrem.documentgenerator.e2etest.TestSupport.validRequest;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource(value = "classpath:application.properties")
@AutoConfigureMockMvc
public class MiniFormAGenerateE2ETest {
    private static final String API_URL = "/version/1/generatePDF";
    private static final String DELETE_API_URL = "/version/1/delete-pdf-document";

    @Autowired
    private MockMvc webClient;

    @Value("${service.pdf-service.uri}")
    private String pdfServiceUri;

    @Value("${service.evidence-management-client-api.uri}")
    private String emClientAPIUri;

    @Value("${service.evidence-management-client-api.delete-uri}")
    private String evidenceManagementDeleteUri;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void before() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void templateNameNotSupplied() throws Exception {
        webClient.perform(post(API_URL)
            .content(templateNotSuppliedRequest())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void templateValuesNotSupplied() throws Exception {
        webClient.perform(post(API_URL)
            .content(templateValuesNotSuppliedRequest())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidJson() throws Exception {
        webClient.perform(post(API_URL)
            .content(invalidRequest().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void pdfServiceRespondsWithBadRequest() throws Exception {
        mockRestServiceServer.expect(once(), requestTo(pdfServiceUri)).andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        webClient.perform(post(API_URL)
            .content(validRequest().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isServiceUnavailable());
    }

    @Test
    public void pdfServiceRespondsUnauthorised() throws Exception {
        mockRestServiceServer.expect(once(), requestTo(pdfServiceUri)).andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        webClient.perform(post(API_URL)
            .content(validRequest().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void documentStoredSuccessfully() throws Exception {
        externalServicesSetUp();

        MvcResult result = webClient.perform(post(API_URL)
            .content(validRequest().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(result.getResponse().getContentAsString(), is(documentResponse()));

        mockRestServiceServer.verify();
    }

    private void externalServicesSetUp() throws JsonProcessingException {
        mockRestServiceServer.expect(once(), requestTo(pdfServiceUri)).andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.OK)
                .body(pdfServiceResponse())
                .contentType(MediaType.APPLICATION_JSON));

        mockRestServiceServer.expect(once(), requestTo(emClientAPIUri)).andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.OK)
                .body(documentStoreServiceResponse())
                .contentType(MediaType.APPLICATION_JSON));
    }
}
