package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenceManagementService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String FILE_PARAMETER = "file";
    private static final String DEFAULT_NAME_FOR_PDF_FILE = "generated-file.pdf";

    private final RestTemplate restTemplate;

    @Value("${service.evidence-management-client-api.uri}")
    private String evidenceManagementEndpoint;

    @Value("${service.evidence-management-client-api.delete-uri}")
    private String evidenceManagementDeleteEndpoint;

    @Value("${service.evidence-management-client-api.download-uri}")
    private String evidenceManagementReadEndpoint;

    public ResponseEntity<byte[]> downloadDocument(String binaryFileUrl, String authorizationToken) {
        log.info("Downloading document from evidence management service for binary url {}", binaryFileUrl);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(evidenceManagementReadEndpoint);
        builder.queryParam("binaryFileUrl", binaryFileUrl);
        ResponseEntity<byte[]> result = restTemplate.exchange(builder.build().encode().toUriString(), HttpMethod.GET,
            new HttpEntity<>(getDownloadHeaders(authorizationToken)), byte[].class, String.class);
        log.info("Documents has been successfully downloaded for binary url {} with status {}", binaryFileUrl, result.getStatusCode());

        return result;
    }

    public FileUploadResponse storeDocument(byte[] document, String fileName, String authorizationToken, String caseTypeId) {
        log.info("Save document call to evidence management is made document of size [{}]", document.length);

        FileUploadResponse fileUploadResponse = save(document, fileName, authorizationToken, caseTypeId);

        return Optional.of(fileUploadResponse)
            .filter(response -> response.getStatus() == HttpStatus.OK)
            .orElseThrow(() -> new DocumentStorageException("Failed to store document"));
    }

    private FileUploadResponse save(byte[] document, String fileName, String authorizationToken, String caseTypeId) {
        requireNonNull(document);

        log.info("evidenceManagementEndpoint [{}], fileName [{}], authorizationToken [{}]",
            evidenceManagementEndpoint, fileName, authorizationToken);

        ResponseEntity<List<FileUploadResponse>> responseEntity = restTemplate.exchange(evidenceManagementEndpoint,
            HttpMethod.POST,
            new HttpEntity<>(buildStoreDocumentRequest(document, fileToBeNamed(fileName)),
                getUploadHeaders(authorizationToken, caseTypeId)), new ParameterizedTypeReference<List<FileUploadResponse>>() {});
        return responseEntity.getBody().get(0);
    }

    public void deleteDocument(String fileUrl, String authorizationToken) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(evidenceManagementDeleteEndpoint);
        builder.queryParam("fileUrl", fileUrl);

        restTemplate.exchange(builder.build().encode().toUriString(), HttpMethod.DELETE,
            new HttpEntity<>(getAuthHttpHeaders(authorizationToken)), String.class);
    }

    private static String fileToBeNamed(String name) {
        return Optional.ofNullable(name).orElse(DEFAULT_NAME_FOR_PDF_FILE);
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

    private HttpHeaders getDownloadHeaders(String authToken) {
        HttpHeaders headers = getAuthHttpHeaders(authToken);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }

    private HttpHeaders getUploadHeaders(String authToken, String caseTypeId) {
        HttpHeaders headers = getAuthHttpHeaders(authToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("caseTypeId", caseTypeId);
        return headers;
    }

    private HttpHeaders getAuthHttpHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, authToken);
        return headers;
    }
}
