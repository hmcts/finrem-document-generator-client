package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DocumentManagementServiceTest {

    private DocumentManagementService service = new DocumentManagementService();

    @Test
    public void generateAndStoreDocument() {
        assertNull(service.generateAndStoreDocument("", new HashMap<>(), ""));
    }

    @Test
    public void storeDocument() {
        assertNull(service.storeDocument(new byte[]{}, ""));
    }

}
