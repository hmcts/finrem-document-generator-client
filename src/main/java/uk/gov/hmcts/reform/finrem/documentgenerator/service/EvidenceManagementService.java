package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.DocumentStorageException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class EvidenceManagementService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String FILE_PARAMETER = "file";
    private static final String DEFAULT_NAME_FOR_PDF_FILE = "MiniFormA.pdf";

    @Value("${service.evidence-management-client-api.uri}")
    private String evidenceManagementEndpoint;

    @Value("${service.evidence-management-client-api.delete-uri}")
    private String evidenceManagementDeleteEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    public FileUploadResponse storeDocument(byte[] document, String authorizationToken) {
        log.info("Save document call to evidence management is made document of size [{}]", document.length);

        FileUploadResponse fileUploadResponse = save(document, authorizationToken);

        if (fileUploadResponse.getStatus() == HttpStatus.OK) {
            return fileUploadResponse;
        } else {
            throw new DocumentStorageException("Failed to store document");
        }
    }

    public void deleteDocument(String fileUrl, String authorizationToken) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(evidenceManagementDeleteEndpoint);
        builder.queryParam("fileUrl", fileUrl);

        restTemplate.exchange(builder.build().encode().toUriString(), HttpMethod.DELETE,
            new HttpEntity<>(getAuthHttpHeaders(authorizationToken)), String.class);
    }

    private FileUploadResponse save(byte[] document, String authorizationToken) {
        requireNonNull(document);

        ResponseEntity<List<FileUploadResponse>> responseEntity = restTemplate.exchange(evidenceManagementEndpoint,
                HttpMethod.POST,
                new HttpEntity<>(
                        buildStoreDocumentRequest(document, DEFAULT_NAME_FOR_PDF_FILE),
                    getHttpHeaders(authorizationToken)),
                new ParameterizedTypeReference<List<FileUploadResponse>>() {
                });

        return responseEntity.getBody().get(0);
    }

    private HttpHeaders getHttpHeaders(String authToken) {
        HttpHeaders headers = getAuthHttpHeaders(authToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private LinkedMultiValueMap<String, Object> buildStoreDocumentRequest(byte[] document, String filename) {
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        HttpEntity<Resource> httpEntity = new HttpEntity<>(new ByteArrayResource(document) {
            @Override
            public String getFilename() {
                return filename;
            }
        }, headers);

        parameters.add(FILE_PARAMETER, httpEntity);
        return parameters;
    }

    private HttpHeaders getAuthHttpHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, authToken);

        return headers;
    }
}
