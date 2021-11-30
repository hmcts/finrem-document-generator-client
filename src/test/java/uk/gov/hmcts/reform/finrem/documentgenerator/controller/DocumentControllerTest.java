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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentControllerTest {

    private static final String FILE_NAME = "file_name";
    private static final String AUTH_TOKEN = "AUTH_TOKEN";

    @Mock
    private DocumentManagementService documentManagementService;

    @InjectMocks
    private DocumentController controller;

    @Test
    public void generatePdfDocument() {
        final String templateName = "templateName";
        final Map<String, Object> placeholder = Collections.emptyMap();

        final Document expected = Document.builder().build();
        when(documentManagementService.storeDocument(templateName, FILE_NAME, placeholder, AUTH_TOKEN))
            .thenReturn(expected);

        Document actual = controller.generatePdf(AUTH_TOKEN, new DocumentRequest(templateName, FILE_NAME, placeholder));

        assertThat(actual, is(expected));
        verify(documentManagementService, times(1))
            .storeDocument(templateName, FILE_NAME, placeholder, AUTH_TOKEN);
    }
}
