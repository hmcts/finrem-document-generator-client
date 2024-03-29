package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintDocument;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.BulkPrintDocumentService;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.BulkPrintService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BulkPrintControllerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private BulkPrintController controller;

    private BulkPrintRequest bulkPrintRequest;

    @Mock
    private BulkPrintService bulkPrintService;

    @Mock
    private BulkPrintDocumentService bulkPrintDocumentService;

    @Test
    public void shouldBulkPrintDocument() {
        UUID randomuuid = UUID.randomUUID();
        bulkPrintRequest = BulkPrintRequest.builder()
            .caseId("1000")
            .letterType("others")
            .bulkPrintDocuments(List.of(BulkPrintDocument.builder().binaryFileUrl("url").fileName("url.pdf").build()))
            .build();
        final List<byte[]> documents = List.of("some random string".getBytes());
        when(bulkPrintDocumentService.downloadDocuments(bulkPrintRequest))
            .thenReturn(documents);

        when(bulkPrintService.send(bulkPrintRequest, documents))
            .thenReturn(randomuuid);

        UUID response = controller.bulkPrint(bulkPrintRequest);
        assertThat(response, is(randomuuid));

        verify(bulkPrintService, times(1)).send(bulkPrintRequest, documents);
    }

    @Test
    public void shouldThrowExceptionOnNoDocumentsDocument() {

        bulkPrintRequest = BulkPrintRequest.builder()
            .caseId("1000")
            .letterType("others")
            .bulkPrintDocuments(Arrays.asList(BulkPrintDocument.builder().binaryFileUrl("url").fileName("url.pdf").build()))
            .build();

        when(bulkPrintDocumentService.downloadDocuments(bulkPrintRequest))
            .thenThrow(new RuntimeException());


        thrown.expect(RuntimeException.class);
        controller.bulkPrint(bulkPrintRequest);
        verifyNoInteractions(bulkPrintService);
    }

    @Test
    public void shouldThrowExceptionOnBulkPrintfailingDocument() {
        bulkPrintRequest = BulkPrintRequest.builder()
            .caseId("1000")
            .letterType("others")
            .bulkPrintDocuments(Arrays.asList(BulkPrintDocument.builder().binaryFileUrl("url").fileName("url.pdf").build()))
            .build();
        final List<byte[]> documents = Arrays.asList("some random string".getBytes());
        when(bulkPrintDocumentService.downloadDocuments(bulkPrintRequest))
            .thenReturn(documents);

        doThrow(new RuntimeException()).when(bulkPrintService).send(bulkPrintRequest, documents);

        thrown.expect(RuntimeException.class);

        controller.bulkPrint(bulkPrintRequest);

        verifyNoMoreInteractions(bulkPrintService);
    }
}
