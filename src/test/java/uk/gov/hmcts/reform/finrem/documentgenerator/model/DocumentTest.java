package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.BINARY_URL;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.FILE_NAME;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.FILE_URL;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;

public class DocumentTest {

    @Test
    public void properties() {
        Document doc = document();

        assertThat(doc.getUrl(), is(equalTo(FILE_URL)));
        assertThat(doc.getBinaryUrl(), is(equalTo(BINARY_URL)));
        assertThat(doc.getFileName(), is(equalTo(FILE_NAME)));
    }
}
