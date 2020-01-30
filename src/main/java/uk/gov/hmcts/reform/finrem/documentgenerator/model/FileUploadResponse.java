package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class FileUploadResponse {

    private String fileUrl;
    private String fileName;
    private String mimeType;
    private String createdBy;
    private String lastModifiedBy;
    private String createdOn;
    private String modifiedOn;
    @NonNull
    private HttpStatus status;
}
