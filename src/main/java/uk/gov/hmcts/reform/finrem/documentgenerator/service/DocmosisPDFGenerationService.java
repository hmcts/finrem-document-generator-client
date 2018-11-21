package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.PDFGenerationException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfDocumentRequest;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class DocmosisPDFGenerationService implements PDFGenerationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.pdf-service.uri}")
    private String pdfServiceEndpoint;

    @Value("${service.pdf-service.accessKey}")
    private String pdfServiceAccessKey;

    @Override
    public byte[] generateDocFrom(String templateName, Map<String, Object> placeholders) {
        if(isNullOrEmpty(templateName)) {
            throw new IllegalArgumentException("document generation template cannot be empty");
        }
        requireNonNull(placeholders);

        log.info("Making request to pdf service to generate pdf document with template "
            + "and placeholders of size [{}]", templateName, placeholders.size());

        try {
            ResponseEntity<byte[]> response = restTemplate.postForEntity(pdfServiceEndpoint,
                request(templateName, placeholders), byte[].class);
            return response.getBody();
        } catch (Exception e) {
            throw new PDFGenerationException("Failed to request PDF from REST endpoint " + e.getMessage(), e);
        }
    }

    private PdfDocumentRequest request(String templateName, Map<String, Object> placeholders) {
        return PdfDocumentRequest.builder()
                .accessKey(pdfServiceAccessKey)
                .templateName(templateName)
                .outputName("result.pdf")
                .data(placeholders).build();
    }
}
