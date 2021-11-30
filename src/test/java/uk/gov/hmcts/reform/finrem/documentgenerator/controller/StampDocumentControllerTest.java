package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.PdfStampingService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;

@RunWith(MockitoJUnitRunner.class)
public class StampDocumentControllerTest {

    private static final String AUTH_TOKEN = "auth";
    private static final String CASE_TYPE = "FinancialRemedyContested";

    @InjectMocks
    private StampDocumentController controller;

    @Mock
    private PdfStampingService pdfStampingService;

    @Test
    public void shouldStampDocument() {
        Document document = document();

        when(pdfStampingService.stampDocument(document, AUTH_TOKEN, false, CASE_TYPE))
            .thenReturn(document);

        Document stampDocument = controller.stampDocument(AUTH_TOKEN, CASE_TYPE, document);

        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
        verify(pdfStampingService, times(1))
            .stampDocument(document, AUTH_TOKEN, false, CASE_TYPE);
    }


    @Test
    public void shouldAnnexAndStampDocument() {
        Document document = document();

        when(pdfStampingService.stampDocument(document, AUTH_TOKEN, true, CASE_TYPE))
            .thenReturn(document);

        Document stampDocument = controller.annexStampDocument(AUTH_TOKEN, CASE_TYPE, document);

        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
        verify(pdfStampingService, times(1))
            .stampDocument(document, AUTH_TOKEN, true, CASE_TYPE);
    }
}
