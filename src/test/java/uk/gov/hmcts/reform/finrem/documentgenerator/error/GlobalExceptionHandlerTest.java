package uk.gov.hmcts.reform.finrem.documentgenerator.error;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GlobalExceptionHandlerTest {

    private static final String SOME_MESSAGE = "some message";
    private final GlobalExceptionHandler classUnderTest = new GlobalExceptionHandler();

    @Test
    public void badRequest() {
        ResponseEntity<Object> response = classUnderTest.handleBadRequestException(new Exception());
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void pdfGenerationExceptionWith301Status() {
        ResponseEntity<Object> response = classUnderTest
                .handlePdfGenerationException(pdfGenerationException(HttpStatus.MOVED_PERMANENTLY));

        assertThat(response.getStatusCode(), is(HttpStatus.MOVED_PERMANENTLY));
        assertThat(response.getBody(), is(GlobalExceptionHandler.SERVER_ERROR_MSG));
    }

    @Test
    public void pdfGenerationExceptionWith400Status() {
        ResponseEntity<Object> response =
                classUnderTest.handlePdfGenerationException(pdfGenerationException(HttpStatus.BAD_REQUEST));

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(GlobalExceptionHandler.SERVER_ERROR_MSG));
    }

    @Test
    public void documentStorageException() {
        DocumentStorageException documentStorageException =
            new DocumentStorageException(SOME_MESSAGE, new Exception());

        ResponseEntity<Object> response =
                classUnderTest.handleDocumentStorageException(documentStorageException);

        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), is(GlobalExceptionHandler.SERVER_ERROR_MSG));
    }

    @Test
    public void clientException() {
        ResponseEntity<Object> response =
            classUnderTest.handleClientException(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), is(GlobalExceptionHandler.CLIENT_ERROR_MSG));
    }

    @Test
    public void serverError() {
        ResponseEntity<Object> response =
            classUnderTest.handleServerError(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), is(GlobalExceptionHandler.SERVER_ERROR_MSG));
    }

    private PDFGenerationException pdfGenerationException(HttpStatus httpStatus) {
        return new PDFGenerationException(SOME_MESSAGE, new HttpClientErrorException(httpStatus));
    }

}
