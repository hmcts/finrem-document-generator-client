package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;

import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;

@Service
@Slf4j
public class DocumentManagementService {

    @Autowired
    private PDFGenerationService pdfGenerationService;

    @Autowired
    private EvidenceManagementService evidenceManagementService;

    private static final Function<FileUploadResponse, Document> CONVERTER = (response -> Document.builder()
        .fileName(response.getFileName())
        .url(response.getFileUrl())
        .binaryUrl(toBinaryUrl(response))
        .mimeType(response.getMimeType())
        .createdOn(response.getCreatedOn())
        .build());

    public Document storeDocument(String templateName,
                                  String fileName,
                                  Map<String, Object> placeholders,
                                  String authorizationToken) {
        log.debug("Generate and Store Document requested with templateName [{}], placeholders of size [{}]",
            templateName, placeholders.size());

        return storeDocument(
            pdfGenerationService.generateDocFrom(templateName, placeholders),
            fileName,
            authorizationToken);
    }

    private Document storeDocument(byte[] document, String fileName, String authorizationToken) {
        log.debug("Store document requested with document of size [{}]", document.length);
        FileUploadResponse response = evidenceManagementService.storeDocument(document, fileName, authorizationToken);

        return CONVERTER.apply(response);
    }

    private static String toBinaryUrl(FileUploadResponse response) {
        return format("%s/binary", response.getFileUrl());
    }
}
