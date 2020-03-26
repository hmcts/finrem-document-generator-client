package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import java.util.Map;

public interface PdfGenerationService {
    byte[] generateDocFrom(String templateName, Map<String, Object> placeholders);
}
