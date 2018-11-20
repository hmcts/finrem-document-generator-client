package uk.gov.hmcts.reform.finrem.documentgenerator.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({PDFGenerationException.class})
    public ResponseEntity<Object> handlePdfGenerationException(Exception exception){
        return handleException(exception);
    }

    @ExceptionHandler({DocumentStorageException.class})
    public ResponseEntity<Object> handleDocumentStorageException(Exception exception){
        return handleException(exception);
    }

    private ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);

        if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
            HttpStatus httpClientErrorException = ((HttpClientErrorException) exception.getCause()).getStatusCode();

            if (httpClientErrorException == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
            } else {
                return ResponseEntity.status(httpClientErrorException).body(exception.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
