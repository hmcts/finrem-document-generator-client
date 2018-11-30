package uk.gov.hmcts.reform.finrem;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class GenerateMiniFormAIntegrationTest {

    @Test
    public void simple() throws Exception {
        Assert.assertEquals(HttpStatus.OK.value(), 200);
    }

}
