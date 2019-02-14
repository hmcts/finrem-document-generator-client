package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.documentgenerator.TestResource;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.CREATED_BY;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.FILE_NAME;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.FILE_URL;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.MIME_TYPE;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.fileUploadResponse;

public class FileUploadResponseTest {

    @Test
    public void properties() {
        FileUploadResponse response = fileUploadResponse();

        assertThat(response.getStatus(), is(equalTo(HttpStatus.OK)));
        assertThat(response.getFileUrl(), is(equalTo(FILE_URL)));
        assertThat(response.getFileName(), is(equalTo(FILE_NAME)));
        assertThat(response.getMimeType(), is(equalTo(MIME_TYPE)));
        assertThat(response.getCreatedBy(), is(equalTo(CREATED_BY)));
        assertThat(response.getLastModifiedBy(), is(equalTo(CREATED_BY)));
        assertThat(response.getCreatedOn(), is(equalTo(TestResource.CREATED_ON)));
        assertThat(response.getModifiedOn(), is(equalTo(TestResource.CREATED_ON)));
    }
}
