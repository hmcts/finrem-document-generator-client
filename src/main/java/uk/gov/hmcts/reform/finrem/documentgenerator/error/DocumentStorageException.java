package uk.gov.hmcts.reform.finrem.documentgenerator.error;

public class DocumentStorageException extends RuntimeException {

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentStorageException(String message) {
        super(message);
    }
}
