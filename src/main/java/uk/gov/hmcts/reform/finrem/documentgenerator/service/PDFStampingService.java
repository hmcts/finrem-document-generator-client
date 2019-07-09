package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.documentgenerator.config.PdfDocumentConfig;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PDFStampingService {

    @Autowired
    private PdfDocumentConfig config;

    public byte[] stampDocument(byte[] inputFileInBytes, byte[] courtSealImage) throws Exception {

        //retrieve doc from DM store
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

        PDDocument doc = PDDocument.load(inputFileInBytes);
        //Retrieving the page
        PDPage page = doc.getPage(0);

        PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, courtSealImage, null);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND,
            true, true);
        contentStream.drawImage(pdImage, 100, 500, 170, 175);
        contentStream.close();

        doc.save(outputBytes);
        doc.close();

        //return document that has been stamped as byte[]
        return outputBytes.toByteArray();
    }

    public byte[] getCourSeal() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/courtseal.png")) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    public static void main(String[] args) throws Exception {
        PDFStampingService service = new PDFStampingService();
        FileInputStream inputFile = new FileInputStream("/Users/chandrashekharkorivi/Downloads/NCB_Test.pdf");
        byte[] courSeal = service.getCourSeal();
        byte[] bytes = service.stampDocument(IOUtils.toByteArray(inputFile), courSeal);
        FileOutputStream fos = new FileOutputStream("/Users/chandrashekharkorivi/Downloads/NCB_Test_Stamped.pdf");
        fos.write(bytes);
        fos.close();
    }
}
