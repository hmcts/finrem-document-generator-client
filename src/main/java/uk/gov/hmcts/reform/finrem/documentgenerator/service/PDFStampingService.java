package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import uk.gov.hmcts.reform.finrem.documentgenerator.GeneratedDocumentInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PDFStampingService {

    public static byte[] stampDocument(Map<String, GeneratedDocumentInfo> generatedDocumentInfoList) throws IOException {
        PDDocument doc = PDDocument.load(generatedDocumentInfoList.get(0).getBytes());

        //get the first page of the document
        PDPage page = doc.getPage(0);

        //retrieve stamp from assets
        PDImageXObject pdImage = PDImageXObject.createFromFile("finrem-document-generator-client/docs/courtSeal.PNG", doc);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND,
            true, true);
        contentStream.drawImage(pdImage, 100, 500, 170, 175);
        contentStream.close();
        doc.close();

        //return document that has been stamped as byte[]
        return new byte[0];
    }
}
