package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class PDFStampingService {

    @Autowired
    private EvidenceManagementService emService;

    public Document stampDocument(Document document, String authorizationToken, boolean isAnnexNeeded) {
        log.info("Stamp document : {}", document);
        try {
            byte[] docInBytes = emService.readDocument(document.getBinaryUrl(), authorizationToken);
            byte[] stampedDoc = stampDocument(docInBytes, isAnnexNeeded);
            FileUploadResponse fileSaved = emService.storeDocument(stampedDoc, document.getFileName(), authorizationToken);
            return CONVERTER.apply(fileSaved);
        } catch (Exception ex) {
            throw new StampDocumentException(format("Failed to stamp PDF for document : %s , exception r : %s",
                document, ex.getMessage()), ex);
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

        //return document that has been stamped as byte[]
        return outputBytes.toByteArray();
    }

    public byte[] imageAsBytes(String fileName) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            return IOUtils.toByteArray(inputStream);
        }
    }

}
