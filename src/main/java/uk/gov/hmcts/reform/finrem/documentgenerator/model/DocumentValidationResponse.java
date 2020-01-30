package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentValidationResponse {

    private String mimeType;
    private List<String> errors;
}
