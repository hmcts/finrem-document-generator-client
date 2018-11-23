package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.BINARY_URL;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.CREATED_ON;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.FILE_NAME;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.MIME_TYPE;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.URL;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;

public class DocumentTest {

    @Test
    public void properties() {
        Document doc = document();

        assertThat(doc.getCreatedOn(), is(equalTo(CREATED_ON)));
        assertThat(doc.getMimeType(), is(equalTo(MIME_TYPE)));
        assertThat(doc.getUrl(), is(equalTo(URL)));
        assertThat(doc.getBinaryUrl(), is(equalTo(BINARY_URL)));
        assertThat(doc.getFileName(), is(equalTo(FILE_NAME)));
    }
}
