package uk.gov.hmcts.reform.finrem.documentgenerator.e2etest;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource(value = "classpath:application.properties")
@AutoConfigureMockMvc
public class DeleteDocumentE2ETest {

    private static final String DELETE_API_URL = "/version/1/delete-pdf-document";

    @Autowired
    private MockMvc webClient;

    @Value("${service.pdf-service.uri}")
    private String pdfServiceUri;

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
    public void documentDeletedSuccessfully() throws Exception {
        documentDeleteServiceSetUp(HttpStatus.NO_CONTENT);

        webClient.perform(delete(DELETE_API_URL)
            .param("fileUrl", "test"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void documentDeleteRequestError() throws Exception {
        documentDeleteServiceSetUp(HttpStatus.INTERNAL_SERVER_ERROR);

        webClient.perform(delete(DELETE_API_URL)
            .param("fileUrl", "test"))
            .andExpect(status().isInternalServerError());
    }

    private void documentDeleteServiceSetUp(HttpStatus status) {
        mockRestServiceServer.expect(once(),
            requestTo(evidenceManagementDeleteUri.concat("?fileUrl=test")))
            .andExpect(method(HttpMethod.DELETE))
            .andRespond(withStatus(status));
    }

}
