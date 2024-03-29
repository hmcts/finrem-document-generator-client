package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintDocument;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintRequest;
import uk.gov.hmcts.reform.sendletter.api.LetterWithPdfsRequest;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BulkPrintServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks private BulkPrintService service;

    @Mock private AuthTokenGenerator authTokenGenerator;
    @Mock private SendLetterApi sendLetterApi;
    private BulkPrintRequest request;

    @Before
    public void setup() {
        request = BulkPrintRequest.builder()
            .caseId("1000")
            .letterType("aa")
            .bulkPrintDocuments(List.of(BulkPrintDocument.builder().binaryFileUrl("abc").fileName("abc.pdf").build()))
            .build();
    }

    @Test
    public void downloadDocuments() {
        UUID randomId = UUID.randomUUID();
        when(authTokenGenerator.generate()).thenReturn("random-string");

        when(sendLetterApi.sendLetter(anyString(), any(LetterWithPdfsRequest.class)))
            .thenReturn(new SendLetterResponse(randomId));

        UUID letterId = service.send(request, singletonList("abc".getBytes()));
        assertThat(letterId, is(equalTo(randomId)));
    }

    @Test
    public void throwsException() {
        when(authTokenGenerator.generate()).thenThrow(new RuntimeException());
        thrown.expect(RuntimeException.class);
        service.send(request, singletonList("abc".getBytes()));
        verifyNoInteractions(sendLetterApi);
    }

    @Test
    public void throwsExceptionOnSendLetter() {
        when(authTokenGenerator.generate())
            .thenReturn("random-string");

        when(sendLetterApi.sendLetter(anyString(), any(LetterWithPdfsRequest.class)))
            .thenThrow(new RuntimeException());

        thrown.expect(RuntimeException.class);
        service.send(request, singletonList("abc".getBytes()));
        verify(authTokenGenerator).generate();
    }
}
