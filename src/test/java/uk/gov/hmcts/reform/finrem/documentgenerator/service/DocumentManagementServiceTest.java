package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.fileUploadResponse;

@RunWith(MockitoJUnitRunner.class)
public class DocumentManagementServiceTest {

    private static final String AUTH_TOKEN = "Bearer BBJHJbbIIBHBLB";
    private static final String CASE_TYPE = "FinancialRemedyContested";
    private static final String FILE_NAME = "kbjh87y8y9JHVKKKJVJ";
    private static final String FILE_URL = "http://dm:80/documents/kbjh87y8y9JHVKKKJVJ";
    public static final ImmutableMap<String, Object> PLACEHOLDERS = ImmutableMap.of("key", "value");
    public static final String TEMPLATE_NAME = "templateName";

    @InjectMocks
    private DocumentManagementService service;

    @Mock
    private PdfGenerationService pdfGenerationService;

    @Mock
    private EvidenceManagementService evidenceManagementService;

    @Before
    public void setUp() {
        when(pdfGenerationService.generateDocFrom(TEMPLATE_NAME, PLACEHOLDERS)).thenReturn("welcome doc".getBytes());
        when(evidenceManagementService.storeDocument("welcome doc".getBytes(), FILE_NAME, AUTH_TOKEN, CASE_TYPE))
            .thenReturn(fileUploadResponse());
    }

    @Test
    public void storeDocument() {
        Document document = service.storeDocument(TEMPLATE_NAME, FILE_NAME, PLACEHOLDERS, AUTH_TOKEN, CASE_TYPE);
        assertThat(document, is(equalTo(document())));
    }

    @Test
    public void deleteDocument() {
        service.deleteDocument(FILE_URL, AUTH_TOKEN);
        verify(evidenceManagementService).deleteDocument(FILE_URL, AUTH_TOKEN);
    }
}
