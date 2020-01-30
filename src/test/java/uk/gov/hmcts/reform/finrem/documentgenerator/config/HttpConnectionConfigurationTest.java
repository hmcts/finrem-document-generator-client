package uk.gov.hmcts.reform.finrem.documentgenerator.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class HttpConnectionConfigurationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate healthCheckRestTemplate;

    @Test
    public void restTemplateBean() {
        assertThat(restTemplate, is(notNullValue()));
        assertThat(healthCheckRestTemplate, is(notNullValue()));
        assertThat(restTemplate, is(not(healthCheckRestTemplate)));
    }
}
