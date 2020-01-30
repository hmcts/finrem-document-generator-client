package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.config.PdfDocumentConfig;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.PDFGenerationException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfDocumentRequest;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocmosisPDFGenerationService implements PDFGenerationService {

    private static final String CASE_DETAILS = "caseDetails";
    private static final String CASE_DATA = "case_data";

    private final RestTemplate restTemplate;
    private final PdfDocumentConfig pdfDocumentConfig;

    @Value("${service.pdf-service.uri}/rs/render")
    private String pdfServiceEndpoint;

    @Value("${service.pdf-service.accessKey}")
    private String pdfServiceAccessKey;

    @Override
    public byte[] generateDocFrom(String templateName, Map<String, Object> placeholders) {
        checkArgument(!isNullOrEmpty(templateName), "document generation template cannot be empty");
        checkNotNull(placeholders, "placeholders map cannot be null");

        log.info("Making request to pdf service to generate pdf document with template [{}], "
            + "placeholders of size [{}], pdfServiceEndpoint [{}] ",
            templateName, placeholders.size(), pdfServiceEndpoint);

        try {
            ResponseEntity<byte[]> response =
                restTemplate.postForEntity(pdfServiceEndpoint, request(templateName, placeholders), byte[].class);
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
                .data(caseData(placeholders)).build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> caseData(Map<String, Object> placeholders) {
        Map<String, Object> data = (Map<String, Object>) ((Map) placeholders.get(CASE_DETAILS)).get(CASE_DATA);
        data.put(pdfDocumentConfig.getDisplayTemplateKey(), pdfDocumentConfig.getDisplayTemplateVal());
        data.put(pdfDocumentConfig.getFamilyCourtImgKey(), pdfDocumentConfig.getFamilyCourtImgVal());
        data.put(pdfDocumentConfig.getHmctsImgKey(), pdfDocumentConfig.getHmctsImgVal());

        return data;
    }
}
