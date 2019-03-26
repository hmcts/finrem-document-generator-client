package uk.gov.hmcts.reform.finrem.documentgenerator.e2etest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import uk.gov.hmcts.reform.finrem.documentgenerator.TestResource;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;

import java.io.File;


final class TestSupport {
    private static final ObjectMapper MAPPER = new ObjectMapper();

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
        final FileUploadResponse fileUploadResponse = TestResource.fileUploadResponse();

        return MAPPER.writeValueAsString(ImmutableList.of(fileUploadResponse));
    }

    static String documentResponse() throws JsonProcessingException {
        return MAPPER.writeValueAsString(TestResource.document());
    }
}
