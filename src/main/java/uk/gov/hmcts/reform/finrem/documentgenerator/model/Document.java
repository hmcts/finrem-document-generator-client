package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "Response body model for document generation")
public class Document {

    @ApiModelProperty(value = "URL to access this document's information")
    private String url;
    @ApiModelProperty(value = "Document's display file name. This will be same as supplied in the request")
    private String fileName;
    @ApiModelProperty(value = "URL to download this document.")
    private String binaryUrl;
}
