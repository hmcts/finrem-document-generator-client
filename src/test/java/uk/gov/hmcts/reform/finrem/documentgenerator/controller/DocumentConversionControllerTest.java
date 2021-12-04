package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentConversionService;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentConversionControllerTest {

    private static final String AUTH_TOKEN = "abc";
    private static final String CASE_TYPE = "FinancialRemedyContested";
    private static final String CONVERTED_FILENAME = "filename.pdf";
    private static final byte[] CONVERTED_BYTES = "abc".getBytes();

    @Mock
    private DocumentConversionService documentConversionService;

    @Mock
    private DocumentManagementService documentManagementService;

    @InjectMocks
    DocumentConversionController documentConversionController;

    private Document documentToConvert = new Document("url", "filename.docx", "binaryUrl");

    @Test
    public void convertsDocumentToPdf() {
        when(documentConversionService.convertDocumentToPdf(documentToConvert, AUTH_TOKEN)).thenReturn(CONVERTED_BYTES);
        when(documentConversionService.getConvertedFilename(eq(documentToConvert.getFileName()))).thenReturn(CONVERTED_FILENAME);
        when(documentManagementService.storeDocument(CONVERTED_BYTES, CONVERTED_FILENAME, AUTH_TOKEN, CASE_TYPE))
            .thenReturn(new Document("newURL", CONVERTED_FILENAME, "newBinaryURL"));

        Document convertedDocument = documentConversionController.convertDocumentToPdf(AUTH_TOKEN, CASE_TYPE, documentToConvert);

        assertThat(convertedDocument.getFileName(), is(CONVERTED_FILENAME));
    }
}
