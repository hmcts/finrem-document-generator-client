package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.util.Map;

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
