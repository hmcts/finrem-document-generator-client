package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteDocumentControllerTest {

    private static final String FILE_URL = "file_url";
    private static final String AUTH_TOKEN = "AUTH_TOKEN";

    @Mock
    private DocumentManagementService documentManagementService;

    @InjectMocks
    private DeleteDocumentController controller;

    @Test
    public void deletePdfDocument() {
        ResponseEntity<Object> response = controller.deleteDocument(AUTH_TOKEN, FILE_URL);
        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));

        verify(documentManagementService, times(1)).deleteDocument(FILE_URL, AUTH_TOKEN);
    }
}
