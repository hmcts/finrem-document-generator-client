package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

public class DocumentRequestTest {

    private static final String TEMPLATE = "template";

    @Test
    public void properties() {
        DocumentRequest request = documentRequest();
        assertThat(request.getTemplate(), is(TEMPLATE));
        assertThat(request.getValues(), is(ImmutableMap.of()));
        assertThat(request.getFileName(), isEmptyString());
    }

    @Test
    public void valueTest() {
        assertThat(documentRequest(), is(equalTo(documentRequest())));
    }

    private DocumentRequest documentRequest() {
        return new DocumentRequest(TEMPLATE, "", ImmutableMap.of());
    }


}
