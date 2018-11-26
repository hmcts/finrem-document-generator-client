package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import javax.validation.constraints.NotBlank;

@Value
@Builder
@ApiModel(description = "Request body model for Document Generation Request")
public class PdfDocumentRequest {

    @ApiModelProperty(value = "Service access key", required = true)
    @JsonProperty(value = "accessKey", required = true)
    @NotBlank
    private final String accessKey;
    @ApiModelProperty(value = "Name of the template", required = true)
    @JsonProperty(value = "templateName", required = true)
    @NotBlank
    private final String templateName;
    @ApiModelProperty(value = "Output file name", required = true)
    @JsonProperty(value = "outputName", required = true)
    @NotBlank
    private final String outputName;
    @JsonProperty(value = "data", required = true)
    @ApiModelProperty(value = "Placeholder key / value pairs", required = true)
    private final Map<String, Object> data;
}
