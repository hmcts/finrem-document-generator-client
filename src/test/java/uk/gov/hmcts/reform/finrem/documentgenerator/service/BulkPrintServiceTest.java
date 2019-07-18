package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.documentgenerator.DocumentGeneratorApplication;
import uk.gov.hmcts.reform.sendletter.api.LetterWithPdfsRequest;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DocumentGeneratorApplication.class)
@TestPropertySource(locations = "/application.properties")
public class BulkPrintServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private BulkPrintService service;

    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @MockBean
    private SendLetterApi sendLetterApi;


    @Test
    public void downloadDocuments() {
        UUID randomId = UUID.randomUUID();
        when(authTokenGenerator.generate()).thenReturn("random-string");

        when(sendLetterApi.sendLetter(anyString(), any(LetterWithPdfsRequest.class)))
            .thenReturn(new SendLetterResponse(randomId));

        UUID letterId = service.send("1000", "aa", Arrays.asList("abc".getBytes()));
        assertThat(letterId, is(equalTo(randomId)));
    }

    @Test
    public void throwsException() {
        when(authTokenGenerator.generate()).thenThrow(new RuntimeException());
        thrown.expect(RuntimeException.class);
        service.send("1000", "aa", Arrays.asList("abc".getBytes()));
        verifyZeroInteractions(sendLetterApi);
    }

    @Test
    public void throwsExceptionOnSendLetter() {
        when(authTokenGenerator.generate())
            .thenReturn("random-string");

        when(sendLetterApi.sendLetter(anyString(), any(LetterWithPdfsRequest.class)))
            .thenThrow(new RuntimeException());

        thrown.expect(RuntimeException.class);
        service.send("1000", "aa", Arrays.asList("abc".getBytes()));
        verify(authTokenGenerator.generate());
    }


}