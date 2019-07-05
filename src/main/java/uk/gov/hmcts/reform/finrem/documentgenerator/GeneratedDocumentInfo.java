package uk.gov.hmcts.reform.finrem.documentgenerator;

import lombok.Data;

@Data
public class GeneratedDocumentInfo {
    private String url;
    private String mimeType;
    private String createdOn;
}
