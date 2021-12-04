package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.DocumentConversionException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class DocumentConversionServiceTest {

    private static final String AUTH_TOKEN = "auth";
    private static final byte[] CONVERTED_BINARY = "converted".getBytes();
    private static final String PDF_SERVICE_URI = "https://doc-gen/rs/convert";

    @Autowired
    private DocumentConversionService documentConversionService;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    EvidenceManagementService evidenceManagementService;

    private MockRestServiceServer mockServer;

    private Document documentToConvert = new Document();

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        documentToConvert.setFileName("file.docx");
        documentToConvert.setUrl("docurl.com");
        documentToConvert.setBinaryUrl("binaryurl.com");
    }

    @Test
    public void convertWordToPdf() {
        mockServer.expect(requestTo(PDF_SERVICE_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(CONVERTED_BINARY, MediaType.APPLICATION_OCTET_STREAM));

        when(evidenceManagementService.downloadDocument(eq(documentToConvert.getBinaryUrl()), eq(AUTH_TOKEN)))
            .thenReturn(ResponseEntity.ok("bytes".getBytes()));

        byte[] result = documentConversionService.convertDocumentToPdf(documentToConvert, AUTH_TOKEN);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(CONVERTED_BINARY));
    }

    @Test(expected = DocumentConversionException.class)
    public void convertWordToPdfFailsWhenAlreadyPdf() {
        mockServer.expect(requestTo(PDF_SERVICE_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(CONVERTED_BINARY, MediaType.APPLICATION_OCTET_STREAM));

        when(evidenceManagementService.downloadDocument(eq(documentToConvert.getBinaryUrl()), eq(AUTH_TOKEN)))
            .thenReturn(ResponseEntity.ok("bytes".getBytes()));

        documentToConvert.setFileName("file.pdf");
        byte[] result = documentConversionService.convertDocumentToPdf(documentToConvert, AUTH_TOKEN);
    }

    @Test
    public void getConvertedFilename() {
        assertThat(documentConversionService.getConvertedFilename("nodot"), is("nodot.pdf"));
        assertThat(documentConversionService.getConvertedFilename("word.docx"), is("word.pdf"));
    }
}
