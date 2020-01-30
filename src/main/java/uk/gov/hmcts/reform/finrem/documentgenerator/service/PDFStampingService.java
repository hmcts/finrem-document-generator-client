package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.StampDocumentException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.FileUploadResponse;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.PDFAnnexStampingInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static java.lang.String.format;
import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;
import static org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromByteArray;
import static uk.gov.hmcts.reform.finrem.documentgenerator.model.PDFAnnexStampingInfo.WIDTH_AND_HEIGHT;
import static uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService.CONVERTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFStampingService {

    private final EvidenceManagementService emService;

    public Document stampDocument(Document document, String authToken, boolean isAnnexNeeded) {
        log.info("Stamp document : {}", document);
        try {
            byte[] docInBytes = emService.downloadDocument(document.getBinaryUrl()).getBody();
            byte[] stampedDoc = stampDocument(docInBytes, isAnnexNeeded);
            FileUploadResponse fileSaved = emService.storeDocument(stampedDoc, document.getFileName(), authToken);
            return CONVERTER.apply(fileSaved);
        } catch (Exception ex) {
            throw new StampDocumentException(format("Failed to annex/stamp PDF for document : %s, "
                + "isAnnexNeeded : %s, Exception  : %s", document, isAnnexNeeded, ex.getMessage()), ex);
        }
    }

    public byte[] stampDocument(byte[] inputDocInBytes, boolean isAnnexNeeded) throws Exception {
        PDDocument doc = PDDocument.load(inputDocInBytes);
        PDPage page = doc.getPage(0);
        PDFAnnexStampingInfo info = PDFAnnexStampingInfo.builder(page).build();
        log.info("PDFAnnexStampingInfo data  = {}", info);

        PDImageXObject annexImage = createFromByteArray(doc, imageAsBytes(info.getAnnexFile()), null);
        PDImageXObject courtSealImage = createFromByteArray(doc, imageAsBytes(info.getCourtSealFile()), null);
        PDPageContentStream psdStream = new PDPageContentStream(doc, page, APPEND, true, true);
        psdStream.drawImage(courtSealImage, info.getCourtSealPositionX(), info.getCourtSealPositionY(),
            WIDTH_AND_HEIGHT, WIDTH_AND_HEIGHT);
        if (isAnnexNeeded) {
            psdStream.drawImage(annexImage, info.getAnnexPositionX(), info.getAnnexPositionY(),
                WIDTH_AND_HEIGHT, WIDTH_AND_HEIGHT);
        }
        psdStream.close();
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        doc.save(outputBytes);
        doc.close();

        return outputBytes.toByteArray();
    }

    public byte[] imageAsBytes(String fileName) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            return IOUtils.toByteArray(inputStream);
        }
    }
}
