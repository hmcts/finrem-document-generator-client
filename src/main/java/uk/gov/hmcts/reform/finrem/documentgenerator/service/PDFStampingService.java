package uk.gov.hmcts.reform.finrem.documentgenerator.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class PDFStampingService {

    //PDFStampingService(PDDocument pdf)
    public static void main (String [] args) throws IOException {

        PDDocument doc = PDDocument.load(new File("/Users/harryh/Documents/original.pdf"));
        //Retrieving the page
        PDPage page = doc.getPage(0);

        PDImageXObject pdImage = PDImageXObject.createFromFile("/Users/harryh/Documents/court_seal.png", doc);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND,
                true, true);
        contentStream.drawImage(pdImage, 100, 500, 170, 175);
        contentStream.close();

        doc.save("/Users/harryh/Documents/StampedDocTEST.pdf");
        doc.close();

        System.out.println("Successfuly Saved Stamped Doc");
    }
}
