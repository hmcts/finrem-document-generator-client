package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CaseDocumentTest {

    @Test
    public void testProperties() {
        CaseDocument caseDocument = caseDocument();
        assertThat(caseDocument.getDocumentBinaryUrl(), is("http://doc1/binary"));
        assertThat(caseDocument.getDocumentFilename(), is("doc1"));
        assertThat(caseDocument.getDocumentUrl(), is("http://doc1"));
    }

    @Test
    public void valueTest() {
        assertThat(caseDocument(), Matchers.is(equalTo(caseDocument())));
    }

    private CaseDocument caseDocument() {
        CaseDocument caseDocument = new CaseDocument();
        caseDocument.setDocumentBinaryUrl("http://doc1/binary");
        caseDocument.setDocumentFilename("doc1");
        caseDocument.setDocumentUrl("http://doc1");
        return caseDocument;
    }
}
