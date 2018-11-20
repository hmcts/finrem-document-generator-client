package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import lombok.Data;

@Data
public class Document {
    private String url;
    private String mimeType;
    private String createdOn;
    private String fileName;
    private String binaryUrl;
}
