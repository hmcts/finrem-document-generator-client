package uk.gov.hmcts.reform.finrem.documentgenerator.service;




import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.documentgenerator.error.DocumentConversionException;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentConversionService {

    private static final String PDF = "pdf";

    @Value("${service.pdf-service.uri}/rs/convert")
    private String documentConversionUrl;

    @Value("${service.pdf-service.accessKey}")
    private String docmosisAccessKey;

    private final RestTemplate restTemplate;

    private final EvidenceManagementService evidenceManagementService;


    public byte[] convertDocumentToPdf(Document sourceDocument) {
        if (sourceDocument.getFileName().toLowerCase().endsWith(PDF)) {
            throw new DocumentConversionException(
                "Document already is a pdf",
                null
            );
        }

        return convert(sourceDocument, PDF);
    }

    public String getConvertedFilename(String filename) {
        return filename.split("\\.")[0] + ".pdf";
    }

    private byte[] convert(Document sourceDocument, String targetFileType) {
        try {
            String filename = getConvertedFilename(sourceDocument.getFileName());
            byte[] docInBytes = evidenceManagementService.downloadDocument(sourceDocument.getBinaryUrl()).getBody();
            File file = new File(filename);
            Files.write(docInBytes, file);

            return restTemplate
                .postForObject(
                    documentConversionUrl,
                    createRequest(file, filename),
                    byte[].class
                );

        } catch (HttpClientErrorException clientEx) {

            throw new DocumentConversionException(
                "Error converting document to pdf",
                clientEx
            );
        } catch (IOException ex) {
            throw new DocumentConversionException(
                "Error creating temp file",
                ex
            );
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> createRequest(
        File file,
        String outputFilename
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("accessKey", docmosisAccessKey);
        body.add("outputName", outputFilename);
        body.add("file", new FileSystemResource(file));

        return new HttpEntity<>(body, headers);
    }
}
