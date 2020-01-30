package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
