package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

public class DocumentRequestTest {

    private static final String TEMPLATE = "template";

    @Test
    public void properties() {
        DocumentRequest request = new DocumentRequest(TEMPLATE, "", ImmutableMap.of());
        assertThat(request.getTemplate(), is(TEMPLATE));
        assertThat(request.getValues(), is(ImmutableMap.of()));
        assertThat(request.getFileName(), isEmptyString());
    }
}
