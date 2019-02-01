package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.document;
import static uk.gov.hmcts.reform.finrem.documentgenerator.TestResource.fileUploadResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class DocumentManagementServiceTest {

    public static final String TEMPLATE_NAME = "templateName";
    public static final ImmutableMap<String, Object> PLACEHOLDERS = ImmutableMap.of("key", "value");
    private static final String AUTH_TOKEN = "Bearer BBJHJbbIIBHBLB";
    private static final String FILE_NAME = "kbjh87y8y9JHVKKKJVJ";

    @Autowired
    private DocumentManagementService service;

    @MockBean
    private PDFGenerationService pdfGenerationService;

    @MockBean
    private EvidenceManagementService evidenceManagementService;

    @Before
    public void setUp() {
        when(pdfGenerationService.generateDocFrom(TEMPLATE_NAME, PLACEHOLDERS)).thenReturn("welcome doc".getBytes());
        when(
            evidenceManagementService.storeDocument("welcome doc".getBytes(), FILE_NAME, AUTH_TOKEN))
            .thenReturn(fileUploadResponse());
    }

    @Test
    public void storeDocument() {
        Document document = service.storeDocument(TEMPLATE_NAME, FILE_NAME, PLACEHOLDERS, AUTH_TOKEN);
        assertThat(document, is(equalTo(document())));
    }
}
