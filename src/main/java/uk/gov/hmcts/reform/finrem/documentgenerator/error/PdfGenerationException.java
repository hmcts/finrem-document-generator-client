package uk.gov.hmcts.reform.finrem.documentgenerator.error;

public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
