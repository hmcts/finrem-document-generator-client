package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DocumentValidationResponseTest {

    @Test
    public void properties() {
        DocumentValidationResponse request = documentValidationResponse();
        assertThat(request.getMimeType(), is("application/pdf"));
        assertThat(request.getErrors(), CoreMatchers.nullValue());
    }

    @Test
    public void valueTest() {
        assertThat(documentValidationResponse(), is(equalTo(documentValidationResponse())));
    }

    private DocumentValidationResponse documentValidationResponse() {
        return DocumentValidationResponse
            .builder()
            .mimeType("application/pdf")
            .build();
    }

}
