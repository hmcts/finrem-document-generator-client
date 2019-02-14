package uk.gov.hmcts.reform.finrem.documentgenerator.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String EXCEPTION_MESSAGE = "Exception occurred : {}";
    static final String CLIENT_ERROR_MSG = "Http Client Exception. "
        + "Please check service input parameters and also verify the status of service token generator";
    static final String SERVER_ERROR_MSG = "Some server side exception occurred. Please check logs for details";

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleClientException(HttpClientErrorException clientErrorException) {

        log.error(EXCEPTION_MESSAGE, clientErrorException.getMessage());

        return ResponseEntity.status(clientErrorException.getStatusCode()).body(CLIENT_ERROR_MSG);
    }

    @ExceptionHandler(value = {ResourceAccessException.class, HttpServerErrorException.class})
    public ResponseEntity<Object> handleServerError(RestClientException restClientException) {

        log.error(EXCEPTION_MESSAGE, restClientException.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVER_ERROR_MSG);
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({PDFGenerationException.class})
    public ResponseEntity<Object> handlePdfGenerationException(Exception exception) {
        return handleException(exception);
    }

    @ExceptionHandler({DocumentStorageException.class})
    public ResponseEntity<Object> handleDocumentStorageException(Exception exception) {
        return handleException(exception);
    }

    private ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);

        if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
            HttpStatus httpClientErrorException = ((HttpClientErrorException) exception.getCause()).getStatusCode();
            return ResponseEntity.status(httpClientErrorException).body(SERVER_ERROR_MSG);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SERVER_ERROR_MSG);
    }
}
