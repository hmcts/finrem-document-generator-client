package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentRequestTest {

    public static final String TEMPLATE = "template";

    @Test
    public void properties() {
        DocumentRequest request = new DocumentRequest(TEMPLATE, ImmutableMap.of());
        assertEquals(TEMPLATE, request.getTemplate());
        assertEquals(ImmutableMap.of(), request.getValues());
    }
}
