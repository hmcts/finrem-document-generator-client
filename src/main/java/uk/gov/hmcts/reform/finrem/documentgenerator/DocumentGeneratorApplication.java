package uk.gov.hmcts.reform.finrem.documentgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uk.gov.hmcts.reform.authorisation.healthcheck.ServiceAuthHealthIndicator;
import uk.gov.hmcts.reform.sendletter.SendLetterAutoConfiguration;

@EnableFeignClients (basePackages = {"uk.gov.hmcts.reform.sendletter"})
@SpringBootApplication(exclude = {SendLetterAutoConfiguration.class})
public class DocumentGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentGeneratorApplication.class, args);
    }

}
