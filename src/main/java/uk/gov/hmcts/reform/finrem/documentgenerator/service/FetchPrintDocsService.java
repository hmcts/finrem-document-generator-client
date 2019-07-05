package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.finrem.documentgenerator.GeneratedDocumentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class FetchPrintDocsService {

    private static final String DOCUMENT_LINK = "DocumentLink";
    private static final String VALUE = "value";
    private static final String DOCUMENT_URL = "document_binary_url";
    private static final String DOCUMENT_TYPE = "DocumentType";
    private static final String DOCUMENT_FILENAME = "document_filename";
    private static final String DOCUMENTS_GENERATED = "DocumentsGenerated";
    @Autowired
    private EvidenceManagementService evidenceManagementService;

    public Map<String, GeneratedDocumentInfo> getGeneratedDocuments(CallbackRequest callbackRequest,
                                                                    String authorizationToken) {
        Map<String, GeneratedDocumentInfo> generatedDocumentInfoList =
            extractGeneratedDocumentList(callbackRequest.getCaseDetails().getData());

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        for (GeneratedDocumentInfo generatedDocumentInfo : generatedDocumentInfoList.values()) {
            ResponseEntity<byte[]> response = evidenceManagementService.readDocument(generatedDocumentInfo.getUrl(),authorizationToken);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Failed to get bytes from document store for document {} in case Id {}",
                    generatedDocumentInfo.getUrl(), caseDetails.getId());
                throw new RuntimeException(String.format("Unexpected code from DM store: %s ", response.getStatusCode()));
            }
            generatedDocumentInfo.setBytes(response.getBody());
        }
        return generatedDocumentInfoList;
    }

    /**
     * I'm not using object mapper here to keep it consistent with rest of code, when we migrate the formatter
     * service to as module dependency this method could be simplified.
     */
    @SuppressWarnings("unchecked")
    private Map<String, GeneratedDocumentInfo> extractGeneratedDocumentList(Map<String, Object> caseData) {
        List<Map> documentList =
            ofNullable(caseData.get(DOCUMENTS_GENERATED)).map(i -> (List<Map>) i).orElse(new ArrayList<>());
        Map<String, GeneratedDocumentInfo> generatedDocumentInfoList = new HashMap<>();
        for (Map<String, Object> document : documentList) {
            Map<String, Object> value = ((Map) document.get(VALUE));
            String documentType = getStringValue(value, DOCUMENT_TYPE);
            Map<String, Object> documentLink = (Map) ofNullable(getValue(value, DOCUMENT_LINK)).orElse(null);

            if (ofNullable(documentLink).isPresent()) {
                GeneratedDocumentInfo gdi = GeneratedDocumentInfo.builder()
                    .documentType(getStringValue(value, DOCUMENT_TYPE))
                    .url(getStringValue(documentLink, DOCUMENT_URL))
                    .documentType(documentType)
                    .fileName(getStringValue(documentLink, DOCUMENT_FILENAME))
                    .build();
                generatedDocumentInfoList.put(documentType, gdi);
            }
        }

        return generatedDocumentInfoList;
    }

    private Object getValue(Map<String, Object> objectMap, String key) {
        Iterator<Map.Entry<String, Object>> iterator = objectMap.entrySet().iterator();
        Object result = null;
        while (iterator.hasNext()) {
            Map.Entry map = iterator.next();
            if (map.getKey().equals(key)) {
                result = map.getValue();
            }
        }
        return result;
    }

    private String getStringValue(Map<String, Object> objectMap, String key) {
        return ofNullable(getValue(objectMap, key)).map(Object::toString).orElse(StringUtils.EMPTY);
    }

}
