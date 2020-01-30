package uk.gov.hmcts.reform.finrem.documentgenerator.model;

import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@Data
public class PDFAnnexStampingInfo {

    public static final String ANNEX_IMAGE = "/annex.png";
    public static final String ANNEX_IMAGE_LEFT_90 = "/annex_left_90.png";
    public static final String ANNEX_IMAGE_UPSIDE_DOWN = "/annex_upside_down.png";
    public static final String ANNEX_IMAGE_RIGHT_90 = "/annex_right_90.png";

    public static final String COURT_SEAL_IMAGE = "/courtseal.png";
    public static final String COURT_SEAL_IMAGE_LEFT_90 = "/courtseal_left_90.png";
    public static final String COURT_SEAL_IMAGE_UPSIDE_DOWN = "/courtseal_upside_down.png";
    public static final String COURT_SEAL_IMAGE_RIGHT_90 = "/courtseal_right_90.png";

    public static final float WIDTH_AND_HEIGHT = 100;

    private float rotation = 0;
    private float annexPositionX = 0;
    private float annexPositionY = 0;
    private float courtSealPositionX = 0;
    private float courtSealPositionY = 0;
    private String annexFile;
    private String courtSealFile;
    private PDPage page;

    private PDFAnnexStampingInfo(PDPage page) {
        this.page = page;
    }

    public static PDFAnnexStampingInfo builder(PDPage page) {
        return new PDFAnnexStampingInfo(page);
    }

    public PDFAnnexStampingInfo build() {
        rotation = page.getRotation();
        PDRectangle box = page.getCropBox();
        float topX = box.getUpperRightX();
        float topY = box.getUpperRightY();
        if (rotation == 0) {
            annexPositionX = topX - (topX / 2) - 40;
            annexPositionY = topY - WIDTH_AND_HEIGHT;
            courtSealPositionX = topX - WIDTH_AND_HEIGHT - 20;
            courtSealPositionY = topY - WIDTH_AND_HEIGHT - 20;
            annexFile = ANNEX_IMAGE;
            courtSealFile = COURT_SEAL_IMAGE;
        } else if (rotation == 90) {
            annexPositionX = topX - WIDTH_AND_HEIGHT;
            annexPositionY = topY - (topY / 2) - 40;
            courtSealPositionX = 20;
            courtSealPositionY = topY - WIDTH_AND_HEIGHT - 20;
            annexFile = ANNEX_IMAGE_LEFT_90;
            courtSealFile = COURT_SEAL_IMAGE_LEFT_90;
        } else if (rotation == 180) {
            annexPositionX = topX - (topX / 2) - 40;
            annexPositionY = 20;
            courtSealPositionX = 20;
            courtSealPositionY = 20;
            annexFile = ANNEX_IMAGE_UPSIDE_DOWN;
            courtSealFile = COURT_SEAL_IMAGE_UPSIDE_DOWN;
        } else if (rotation == 270) {
            annexPositionX = topX - WIDTH_AND_HEIGHT;
            annexPositionY = topY - (topY / 2) - 40;
            courtSealPositionX = topX - WIDTH_AND_HEIGHT - 20;
            courtSealPositionY = 20;
            annexFile = ANNEX_IMAGE_RIGHT_90;
            courtSealFile = COURT_SEAL_IMAGE_RIGHT_90;
        }
        return this;
    }

}
