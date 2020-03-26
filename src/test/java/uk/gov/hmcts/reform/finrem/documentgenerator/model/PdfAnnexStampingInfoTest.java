package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.ANNEX_IMAGE;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.ANNEX_IMAGE_LEFT_90;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.ANNEX_IMAGE_RIGHT_90;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.ANNEX_IMAGE_UPSIDE_DOWN;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.COURT_SEAL_IMAGE;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.COURT_SEAL_IMAGE_LEFT_90;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.COURT_SEAL_IMAGE_RIGHT_90;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PdfAnnexStampingInfo.COURT_SEAL_IMAGE_UPSIDE_DOWN;


@RunWith(MockitoJUnitRunner.class)
public class PdfAnnexStampingInfoTest {

    private PdfAnnexStampingInfo stampingInfo;
    private PDPage page = mock(PDPage.class);
    private PDRectangle box = mock(PDRectangle.class);

    @Before
    public void setUp() {
        stampingInfo = PdfAnnexStampingInfo.builder(page);

        when(page.getCropBox()).thenReturn(box);
        when(box.getUpperRightX()).thenReturn(1000f);
        when(box.getUpperRightY()).thenReturn(1000f);
    }

    @Test
    public void shouldBuildWithCorrectPositionsWhenRotationIsZero() {
        when(page.getRotation()).thenReturn(0);

        PdfAnnexStampingInfo info = stampingInfo.build();

        assertThat(info.getAnnexFile(), is(ANNEX_IMAGE));
        assertThat(info.getCourtSealFile(), is(COURT_SEAL_IMAGE));
        assertThat(info.getAnnexPositionX(), is(460f));
        assertThat(info.getAnnexPositionY(), is(900f));
        assertThat(info.getCourtSealPositionX(), is(880f));
        assertThat(info.getCourtSealPositionY(), is(880f));
    }


    @Test
    public void shouldBuildWithCorrectPositionsWhenRotationIs90() {
        when(page.getRotation()).thenReturn(90);

        PdfAnnexStampingInfo info = stampingInfo.build();

        assertThat(info.getAnnexFile(), is(ANNEX_IMAGE_LEFT_90));
        assertThat(info.getCourtSealFile(), is(COURT_SEAL_IMAGE_LEFT_90));
        assertThat(info.getAnnexPositionX(), is(900f));
        assertThat(info.getAnnexPositionY(), is(460f));
        assertThat(info.getCourtSealPositionX(), is(20f));
        assertThat(info.getCourtSealPositionY(), is(880f));
    }

    @Test
    public void shouldBuildWithCorrectPositionsWhenRotationIs180() {
        when(page.getRotation()).thenReturn(180);

        PdfAnnexStampingInfo info = stampingInfo.build();

        assertThat(info.getAnnexFile(), is(ANNEX_IMAGE_UPSIDE_DOWN));
        assertThat(info.getCourtSealFile(), is(COURT_SEAL_IMAGE_UPSIDE_DOWN));
        assertThat(info.getAnnexPositionX(), is(460f));
        assertThat(info.getAnnexPositionY(), is(20f));
        assertThat(info.getCourtSealPositionX(), is(20f));
        assertThat(info.getCourtSealPositionY(), is(20f));
    }

    @Test
    public void shouldBuildWithCorrectPositionsWhenRotationIs270() {
        when(page.getRotation()).thenReturn(270);

        PdfAnnexStampingInfo info = stampingInfo.build();

        assertThat(info.getAnnexFile(), is(ANNEX_IMAGE_RIGHT_90));
        assertThat(info.getCourtSealFile(), is(COURT_SEAL_IMAGE_RIGHT_90));
        assertThat(info.getAnnexPositionX(), is(900f));
        assertThat(info.getAnnexPositionY(), is(460f));
        assertThat(info.getCourtSealPositionX(), is(880f));
        assertThat(info.getCourtSealPositionY(), is(20f));
    }
}
