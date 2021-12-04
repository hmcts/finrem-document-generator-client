package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.StampDocumentException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.fileUploadResponse;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.COURT_SEAL_IMAGE;

@RunWith(MockitoJUnitRunner.class)
public class PdfStampingServiceTest {

    private static final String AUTH_TOKEN = "auth";
    private static final String CASE_TYPE = "FinancialRemedyContested";
    private static final String COURT_SEAL_PDF = "/courtseal.pdf";

    @InjectMocks
    private PdfStampingService service;

    @Mock
    private EvidenceManagementService evidenceManagementService;

    @Test(expected = StampDocumentException.class)
    public void shouldThrowExceptionWhenDocumentIsNotPdf() throws Exception {
        Document document = document();
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_IMAGE);

        when(evidenceManagementService.downloadDocument(document.getBinaryUrl(), AUTH_TOKEN))
            .thenReturn(ResponseEntity.ok(imageAsBytes));

        service.stampDocument(document, "auth", false, CASE_TYPE);
    }

    @Test
    public void shouldAddAnnexAndStampToDocument() throws Exception {
        Document document = document();
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);

        when(evidenceManagementService.downloadDocument(document.getBinaryUrl(), AUTH_TOKEN))
            .thenReturn(ResponseEntity.ok(imageAsBytes));

        when(evidenceManagementService.storeDocument(any(), anyString(), anyString(), anyString()))
            .thenReturn(fileUploadResponse());

        Document stampDocument = service.stampDocument(document, "auth", true, CASE_TYPE);

        assertThat(stampDocument, not(equalTo(imageAsBytes)));
        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
    }

    @Test
    public void shouldAddStampToDocument() throws Exception {
        Document document = document();
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);

        when(evidenceManagementService.downloadDocument(document.getBinaryUrl(), AUTH_TOKEN))
            .thenReturn(ResponseEntity.ok(imageAsBytes));

        when(evidenceManagementService.storeDocument(any(), anyString(), anyString(), anyString()))
            .thenReturn(fileUploadResponse());

        Document stampDocument = service.stampDocument(document, "auth", false, CASE_TYPE);

        assertThat(stampDocument, not(equalTo(imageAsBytes)));
        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenInputIsNotPdf() throws Exception {
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_IMAGE);

        service.stampDocument(imageAsBytes, false);
    }

    @Test
    public void shouldAnnexAndStampPdfWithCourSeal() throws Exception {
        byte[] imageAsBytes = service.imageAsBytes(COURT_SEAL_PDF);
        byte[] stampDocument = service.stampDocument(imageAsBytes, true);

        assertThat(stampDocument, notNullValue());
        assertThat(stampDocument, not(equalTo(imageAsBytes)));
    }

    @Test
    public void shouldStampPdfWithCourtSeal() throws Exception {
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
