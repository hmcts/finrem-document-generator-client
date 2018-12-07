package uk.gov.hmcts.reform.finrem.documentgenerator.e2etest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;

import java.io.File;

import static java.lang.String.format;

final class TestSupport {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String FILE_NAME = "name";
    private static final String FILE_URL = "url";
    private static final String MIME_TYPE = "app/pdf";
    private static final String CREATED_ON = "20th October 2018";
    private static final String CREATED_BY = "user";

    static JsonNode invalidRequest() throws Exception {
        return MAPPER.readTree(new File(TestSupport.class
            .getResource("/fixtures/invalid-doc-request.json").toURI()));
    }

    static JsonNode validRequest() throws Exception {
        return MAPPER.readTree(new File(TestSupport.class
            .getResource("/fixtures/valid-doc-request.json").toURI()));
    }

    static String templateValuesNotSuppliedRequest() throws JsonProcessingException {
        return MAPPER.writeValueAsString(new DocumentRequest("test", null));
    }

    static String templateNotSuppliedRequest() throws JsonProcessingException {
        return MAPPER.writeValueAsString(new DocumentRequest(null, ImmutableMap.of()));
    }

    static byte[] pdfServiceResponse() throws JsonProcessingException {
        return MAPPER.writeValueAsBytes(new byte[]{1});
    }

    static String documentStoreServiceResponse() throws JsonProcessingException {
        final FileUploadResponse fileUploadResponse = fileUploadResponse();

        return MAPPER.writeValueAsString(ImmutableList.of(fileUploadResponse));
    }

    static String documentResponse() throws JsonProcessingException {
        return MAPPER.writeValueAsString(document());
    }

    private static Document document() {
        return Document.builder()
            .mimeType(MIME_TYPE)
            .createdOn(CREATED_ON)
            .fileName(FILE_NAME)
            .url(FILE_URL)
            .binaryUrl(format("%s/binary", FILE_URL)).build();
    }

    private static FileUploadResponse fileUploadResponse() {
        final FileUploadResponse fileUploadResponse = new FileUploadResponse(HttpStatus.OK);
        fileUploadResponse.setFileName(FILE_NAME);
        fileUploadResponse.setFileUrl(FILE_URL);
        fileUploadResponse.setMimeType(MIME_TYPE);
        fileUploadResponse.setCreatedOn(CREATED_ON);
        fileUploadResponse.setCreatedBy(CREATED_BY);
        return fileUploadResponse;
    }
}
