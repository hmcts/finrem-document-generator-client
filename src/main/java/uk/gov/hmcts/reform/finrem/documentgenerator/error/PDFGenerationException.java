package uk.gov.hmcts.reform.finrem.documentgenerator.error;

public class PDFGenerationException extends RuntimeException {

    public PDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
