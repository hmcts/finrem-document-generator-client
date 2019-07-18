package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.StampDocumentException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.fileUploadResponse;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PDFAnnexStampingInfo.COURT_SEAL_IMAGE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class PDFStampingServiceTest {
    public static final String COURT_SEAL_PDF = "/courtseal.pdf";

    @Autowired
    private PDFStampingService service;

    @MockBean
    private EvidenceManagementService evidenceManagementService;


    @Test(expected = StampDocumentException.class)
    public void shouldThrowExceptionWhenDocumentIsNotPDF() throws Exception {
        Document document = document();
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_IMAGE);
        when(evidenceManagementService.downloadDocument(document.getBinaryUrl()))
            .thenReturn(ResponseEntity.ok(imageAsBytes));

        service.stampDocument(document, "auth", false);
    }

    @Test
    public void shouldAddAnnexAndStampToDocument() throws Exception {
        Document document = document();
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);

        when(evidenceManagementService.downloadDocument(document.getBinaryUrl()))
            .thenReturn(ResponseEntity.ok(imageAsBytes));

        when(evidenceManagementService.storeDocument(any(), anyString(), anyString()))
            .thenReturn(fileUploadResponse());

        Document stampDocument = service.stampDocument(document, "auth", true);

        assertThat(stampDocument, not(equalTo(imageAsBytes)));
        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
    }


    @Test
    public void shouldAddStampToDocument() throws Exception {
        Document document = document();
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);

        when(evidenceManagementService.downloadDocument(document.getBinaryUrl()))
            .thenReturn(ResponseEntity.ok(imageAsBytes));

        when(evidenceManagementService.storeDocument(any(), anyString(), anyString()))
            .thenReturn(fileUploadResponse());

        Document stampDocument = service.stampDocument(document, "auth", false);

        assertThat(stampDocument, not(equalTo(imageAsBytes)));
        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenInputIsNotPDF() throws Exception {
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_IMAGE);

        service.stampDocument(imageAsBytes, false);
    }

    @Test
    public void shouldAnnexAndStampPDFWithCourSeal() throws Exception {
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);

        byte[] stampDocument = service.stampDocument(imageAsBytes, true);

        assertThat(stampDocument, notNullValue());
        assertThat(stampDocument, not(equalTo(imageAsBytes)));
    }

    @Test
    public void shouldStampPDFWithCourSeal() throws Exception {
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);

        byte[] stampDocument = service.stampDocument(imageAsBytes, false);

        assertThat(stampDocument, notNullValue());
        assertThat(stampDocument, not(equalTo(imageAsBytes)));
    }


    @Test
    public void shouldGetImageAsBytes() throws Exception {
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_IMAGE);

        assertThat(imageAsBytes, notNullValue());
    }

}
