package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import javax.validation.constraints.NotBlank;

@Value
@Builder
public class PdfDocumentRequest {
    @JsonProperty(value = "accessKey", required = true)
    @NotBlank
    private final String accessKey;
    @JsonProperty(value = "templateName", required = true)
    @NotBlank
    private final String templateName;
    @JsonProperty(value = "outputName", required = true)
    @NotBlank
    private final String outputName;
    @JsonProperty(value = "data", required = true)
    private final Map<String, Object> data;
}
