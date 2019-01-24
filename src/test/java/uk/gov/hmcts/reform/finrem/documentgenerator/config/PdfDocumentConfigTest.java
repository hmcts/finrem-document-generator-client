package uk.gov.hmcts.reform.finrem.documentgenerator.config;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import uk.gov.hmcts.reform.finrem.documentgenerator.config.PdfDocumentConfig;


public class PdfDocumentConfigTest {

    public static final String TEMPLATE_KEY = "templateKey";
    public static final String TEMPLATE_VAL = "templateVal";
    public static final String IMG_KEY = "imgKey";
    public static final String IMG_VAL = "imgVal";
    public static final String HMCTS_IMG_KEY = "hmctsImgKey";
    public static final String HMCTS_IMG_VAL = "hmctsImgVal";

    private final PdfDocumentConfig config = new PdfDocumentConfig();

    @Test
    public void properties() {
        setters();
        getters();
    }

    private void getters() {
        assertThat(config.getDisplayTemplateKey(), is(TEMPLATE_KEY));
        assertThat(config.getDisplayTemplateVal(), is(TEMPLATE_VAL));
        assertThat(config.getFamilyCourtImgKey(), is(IMG_KEY));
        assertThat(config.getFamilyCourtImgVal(), is(IMG_VAL));
        assertThat(config.getHmctsImgKey(), is(HMCTS_IMG_KEY));
        assertThat(config.getHmctsImgVal(), is(HMCTS_IMG_VAL));
    }

    private void setters() {
        config.setDisplayTemplateKey(TEMPLATE_KEY);
        config.setDisplayTemplateVal(TEMPLATE_VAL);
        config.setFamilyCourtImgKey(IMG_KEY);
        config.setFamilyCourtImgVal(IMG_VAL);
        config.setHmctsImgKey(HMCTS_IMG_KEY);
        config.setHmctsImgVal(HMCTS_IMG_VAL);
    }
}
