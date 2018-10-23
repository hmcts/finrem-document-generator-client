package uk.gov.hmcts.reform.finrem.documentgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.hmcts.reform.authorisation.healthcheck.ServiceAuthHealthIndicator;

@SpringBootApplication
public class DocumentGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentGeneratorApplication.class, args);
    }
}
