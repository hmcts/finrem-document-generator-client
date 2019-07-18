package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.PDFStampingService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;

@RunWith(MockitoJUnitRunner.class)
public class StampDocumentControllerTest {
    private static String AUTH_TOKEN = "auth";


    @InjectMocks
    private StampDocumentController controller;

    @Mock
    private PDFStampingService pdfStampingService;


    @Test
    public void shouldStampDocument() {
        Document document = document();

        when(pdfStampingService.stampDocument(document, AUTH_TOKEN, false))
            .thenReturn(document);

        Document stampDocument = controller.stampDocument(AUTH_TOKEN, document);

        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
        verify(pdfStampingService, times(1))
            .stampDocument(document, AUTH_TOKEN, false);
    }


    @Test
    public void shouldAnnexAndStampDocument() {
        Document document = document();

        when(pdfStampingService.stampDocument(document, AUTH_TOKEN, true))
            .thenReturn(document);

        Document stampDocument = controller.annexStampDocument(AUTH_TOKEN, document);

        assertThat(stampDocument.getFileName(), is(document.getFileName()));
        assertThat(stampDocument.getBinaryUrl(), is(document.getBinaryUrl()));
        assertThat(stampDocument.getUrl(), is(document.getUrl()));
        verify(pdfStampingService, times(1))
            .stampDocument(document, AUTH_TOKEN, true);
    }
}
