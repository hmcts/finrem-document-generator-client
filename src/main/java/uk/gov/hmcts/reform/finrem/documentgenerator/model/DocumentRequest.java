package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.Map;
import javax.validation.constraints.NotBlank;

@Value
@ApiModel(description = "Request body model for Document Generation Request")
public class DocumentRequest {
    @ApiModelProperty(value = "Name of the template", required = true)
    @JsonProperty(value = "template", required = true)
    @NotBlank
    private final String template;
    @ApiModelProperty(value = "Name of the file")
    @JsonProperty(value = "fileName", required = true)
    private final String fileName;
    @JsonProperty(value = "values", required = true)
    @ApiModelProperty(value = "Placeholder key / value pairs", required = true)
    private final Map<String, Object> values;
}
