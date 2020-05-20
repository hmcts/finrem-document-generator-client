package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintDocument;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintRequest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class BulkPrintDocumentServiceTest {


    private static final String FILE_URL = "http://dm:80/documents/kbjh87y8y9JHVKKKJVJ";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    private BulkPrintDocumentService service;
    @MockBean
    private EvidenceManagementService evidenceManagementService;
    private byte[] someBytes = "ainhsdcnoih".getBytes();

    @Test
    public void downloadDocuments() {
        when(
            evidenceManagementService.downloadDocument(FILE_URL))
            .thenReturn(ResponseEntity.ok(someBytes));

        BulkPrintRequest bulkPrintRequest = BulkPrintRequest.builder()
            .bulkPrintDocuments(Arrays.asList(BulkPrintDocument.builder().binaryFileUrl(FILE_URL).build()))
            .build();

        List<byte[]> result = service.downloadDocuments(bulkPrintRequest);
        assertThat(result.get(0), is(equalTo(someBytes)));
    }

    @Test
    public void throwsExceptionOnBadRequest() {
        when(
            evidenceManagementService.downloadDocument(FILE_URL))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(someBytes));

        BulkPrintRequest bulkPrintRequest = BulkPrintRequest.builder()
            .bulkPrintDocuments(Arrays.asList(BulkPrintDocument.builder().binaryFileUrl(FILE_URL).build()))
            .build();

        thrown.expect(RuntimeException.class);
        service.downloadDocuments(bulkPrintRequest);
    }

    @Test
    public void throwsExceptionOnAnyException() {
        when(
            evidenceManagementService.downloadDocument(FILE_URL))
            .thenThrow(new RuntimeException());

        BulkPrintRequest bulkPrintRequest = BulkPrintRequest.builder()
            .bulkPrintDocuments(Arrays.asList(BulkPrintDocument.builder().binaryFileUrl(FILE_URL).build()))
            .build();

        thrown.expect(RuntimeException.class);
        service.downloadDocuments(bulkPrintRequest);
    }
}
