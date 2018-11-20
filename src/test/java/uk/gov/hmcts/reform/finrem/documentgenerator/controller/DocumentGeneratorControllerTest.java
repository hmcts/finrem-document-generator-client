package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfDocumentRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentGeneratorControllerTest {

    @Mock
    private DocumentManagementService documentManagementService;

    @InjectMocks
    private DocumentGeneratorController classUnderTest;

    @Test
    public void whenGeneratePDF_thenReturnGeneratedPDFDocumentInfo() {
        final String templateName = "templateName";
        final Map<String, Object> placeholder = Collections.emptyMap();

        final Document expected = new Document();

        when(documentManagementService.generateAndStoreDocument(templateName, placeholder, "testToken"))
            .thenReturn(expected);

        Document actual = classUnderTest
            .generatePDF("testToken", new DocumentRequest(templateName, placeholder));

        assertEquals(expected, actual);

        verify(documentManagementService, times(1))
            .generateAndStoreDocument(templateName, placeholder, "testToken");
    }
}
