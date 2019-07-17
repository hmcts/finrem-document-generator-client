package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentValidationResponse;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentValidationService;

import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DocumentValidationControllerTest {
    private static final String AUTH_TOKEN = "AUTH_TOKEN";
    private static final String FILE_BINARY_URL = "http://dm-store:8080/eeww123456/binary";

    @Mock
    private DocumentValidationService service;
    @InjectMocks
    private DocumentValidationController underTest;

    @Test
    public void shouldReturnSuccessfulFileTypeCheckWithOutErrors() {
        when(service.validateFileType(FILE_BINARY_URL))
            .thenReturn(DocumentValidationResponse.builder().build());
        DocumentValidationResponse documentValidationResponse = underTest.checkUploadedFileType(AUTH_TOKEN, FILE_BINARY_URL);
        assertThat(documentValidationResponse.getErrors(), CoreMatchers.nullValue());
    }

    @Test
    public void shouldReturnInvalidFileTypeCheckWithErrors() {
        when(service.validateFileType(FILE_BINARY_URL))
            .thenReturn(DocumentValidationResponse.builder()
                .errors(singletonList("Invalid Mime Type")).build());
        DocumentValidationResponse documentValidationResponse = underTest.checkUploadedFileType(AUTH_TOKEN, FILE_BINARY_URL);
        assertThat(documentValidationResponse.getErrors(), is(singletonList("Invalid Mime Type")));
    }
}
