package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentValidationResponse;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentValidationResponse.DocumentValidationResponseBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentValidationService {

    private final EvidenceManagementService service;

    @Value("#{'${document.validation.mimeTypes}'.split(',')}")
    private List<String> mimeTypes;

    @Value("${document.validation.fileUploadErrorMessage}")
    private String fileUploadErrorMessage;

    private Tika tika = new Tika();

    public DocumentValidationResponse validateFileType(String fileBinaryUrl) {
        ResponseEntity<byte[]> responseEntity = service.downloadDocument(fileBinaryUrl);
        DocumentValidationResponseBuilder builder = DocumentValidationResponse.builder();
        if (Objects.isNull(responseEntity.getBody())) {
            builder.errors(singletonList("Downloaded document is empty"));
        } else {
            InputStream targetStream = new ByteArrayInputStream(responseEntity.getBody());
            try {
                String detect = tika.detect(targetStream, new Metadata());
                if (mimeTypes.contains(detect)) {
                    builder.mimeType(detect);
                } else {
                    builder.errors(singletonList(fileUploadErrorMessage))
                        .mimeType(detect);
                }
            } catch (IOException ex) {
                log.error("Unable to detect the MimeType due to IOException", ex.getMessage());
                builder.errors(singletonList("Unable to detect the MimeType due to IOException"));
            }
        }

        return builder.build();
    }
}
