package uk.gov.hmcts.reform.finrem.documentgenerator.config;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SwaggerConfigurationTest {

    @Test
    public void docketBean() {
        assertThat(new SwaggerConfiguration().api(), is(notNullValue()));
    }
}
