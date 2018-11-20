package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import java.util.Map;

@Service
@Slf4j
public class DocumentManagementService {

    @Autowired
    private PDFGenerationService pdfGenerationService;

    public Document generateAndStoreDocument(String templateName,
                                             Map<String, Object> placeholders,
                                             String authorizationToken) {
        log.debug("Generate and Store Document requested with templateName [{}], placeholders of size [{}]",
            templateName, placeholders.size());

        return storeDocument(pdfGenerationService.generateDocFrom(templateName, placeholders), authorizationToken);
    }

    public Document storeDocument(byte[] document, String authorizationToken) {
        return null;
    }
}
