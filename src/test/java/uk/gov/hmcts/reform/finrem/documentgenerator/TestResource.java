package uk.gov.hmcts.reform.finrem.documentgenerator;

import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;

import static java.lang.String.format;

public class TestResource {

    public static final String URL = "some_url";
    public static final String BINARY_URL = format("%s/binary", URL);
    public static final String FILE_NAME = "file_name";
    public static final String CREATED_ON = "11 October 2018";
    public static final String MIME_TYPE = "application/pdf";
    public static final String CREATED_BY = "anonymous";

    public static FileUploadResponse fileUploadResponse() {
        FileUploadResponse response = new FileUploadResponse(HttpStatus.OK);
        response.setFileUrl(URL);
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
            .url(URL)
            .fileName(FILE_NAME)
            .binaryUrl(BINARY_URL)
            .createdOn(CREATED_ON)
            .mimeType(MIME_TYPE)
            .build();
    }
}
