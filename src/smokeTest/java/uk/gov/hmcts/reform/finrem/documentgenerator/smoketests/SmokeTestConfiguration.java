package uk.gov.hmcts.reform.finrem.documentgenerator.smoketests;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan("uk.gov.hmcts.reform.finrem.documentgenerator")
@PropertySource("application.properties")
public class SmokeTestConfiguration {
}
