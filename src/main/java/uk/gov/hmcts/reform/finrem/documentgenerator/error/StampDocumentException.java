package uk.gov.hmcts.reform.finrem.documentgenerator.error;

public class StampDocumentException extends RuntimeException {

    public StampDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
