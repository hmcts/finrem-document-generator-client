package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentControllerTest {

    public static final String FILE_URL = "file_url";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    @Mock
    private DocumentManagementService documentManagementService;

    @InjectMocks
    private DocumentController controller;

    @Test
    public void generatePdfDocument() {
        final String templateName = "templateName";
        final Map<String, Object> placeholder = Collections.emptyMap();

        final Document expected = Document.builder().build();
        when(documentManagementService.storeDocument(templateName, placeholder, AUTH_TOKEN)).thenReturn(expected);

        Document actual = controller.generatePDF(AUTH_TOKEN, new DocumentRequest(templateName, placeholder));

        assertThat(actual, is(expected));
        verify(documentManagementService, times(1)).storeDocument(templateName, placeholder, AUTH_TOKEN);
    }
}
