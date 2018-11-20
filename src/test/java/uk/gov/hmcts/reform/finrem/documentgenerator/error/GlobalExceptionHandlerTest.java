package uk.gov.hmcts.reform.finrem.documentgenerator.error;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler classUnderTest = new GlobalExceptionHandler();

    @Test
    public void badRequest() {
        final Exception exception = new Exception();

        ResponseEntity<Object> response = classUnderTest.handleBadRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void pdfGenerationExceptionWith301Status() {
        final HttpStatus httpStatus = HttpStatus.MOVED_PERMANENTLY;

        final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(httpStatus);
        final String message = "some message";

        final PDFGenerationException pdfGenerationException =
                new PDFGenerationException(message, httpClientErrorException);

        ResponseEntity<Object> response = classUnderTest
                .handlePdfGenerationException(pdfGenerationException);

        assertEquals(httpStatus, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    public void pdfGenerationExceptionWith400Status() {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(httpStatus);
        final String message = "some message";

        final PDFGenerationException pdfGenerationException =
                new PDFGenerationException(message, httpClientErrorException);

        ResponseEntity<Object> response =
                classUnderTest.handlePdfGenerationException(pdfGenerationException);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    public void documentStorageException() {
        final String message = "some message";
        final Exception exception = new Exception();

        DocumentStorageException documentStorageException = new DocumentStorageException(message, exception);

        ResponseEntity<Object> response =
                classUnderTest.handleDocumentStorageException(documentStorageException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}
