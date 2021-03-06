package uk.gov.hmcts.reform.finrem.documentgenerator;

import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;

import static java.lang.String.format;

public class TestResource {

    public static final String FILE_URL = "url";
    public static final String BINARY_URL = format("%s/binary", FILE_URL);
    public static final String FILE_NAME = "name";
    public static final String CREATED_ON = "20th October 2018";
    public static final String MIME_TYPE = "app/pdf";
    public static final String CREATED_BY = "user";

    public static FileUploadResponse fileUploadResponse() {
        FileUploadResponse response = new FileUploadResponse(HttpStatus.OK);
        response.setFileUrl(FILE_URL);
        response.setFileName(FILE_NAME);
        response.setMimeType(MIME_TYPE);
        response.setCreatedOn(CREATED_ON);
        response.setLastModifiedBy(CREATED_BY);
        response.setModifiedOn(CREATED_ON);
        response.setCreatedBy(CREATED_BY);
        return response;
    }

    public static Document document() {
        return Document.builder()
            .url(FILE_URL)
            .fileName(FILE_NAME)
            .binaryUrl(BINARY_URL)
            .build();
    }
}
