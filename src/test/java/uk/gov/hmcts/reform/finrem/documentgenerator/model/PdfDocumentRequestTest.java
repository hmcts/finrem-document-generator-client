package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class PdfDocumentRequestTest {

    private static final String TEMPLATE_NAME = "TEMPLATE_NAME";
    private static final String ACCESS_KEY = "TEST_KEY";
    private static final String OUTPUT_NAME = "TEST_NAME";

    @Test
    public void properties() {
        PdfDocumentRequest request = pdfRequest();

        assertThat(request.getAccessKey(), is(ACCESS_KEY));
        assertThat(request.getOutputName(), is(OUTPUT_NAME));
        assertThat(request.getTemplateName(), is(TEMPLATE_NAME));
        assertThat(request.getData(), is(ImmutableMap.of()));
    }

    @Test
    public void equality() {
        assertThat(pdfRequest(), is(equalTo(pdfRequest())));
        assertThat(pdfRequest(), is(not(equalTo(pdfRequest("kjdwede")))));
    }

    private PdfDocumentRequest pdfRequest() {
        return pdfRequest(ACCESS_KEY);
    }

    private PdfDocumentRequest pdfRequest(String accessKey) {
        return PdfDocumentRequest
            .builder()
            .accessKey(accessKey)
            .outputName(OUTPUT_NAME)
            .templateName(TEMPLATE_NAME)
            .data(ImmutableMap.of()).build();
    }
}
