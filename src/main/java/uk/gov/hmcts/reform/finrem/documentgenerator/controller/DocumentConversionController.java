package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentConversionService;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

@RestController
@RequiredArgsConstructor
@Api(value = "Document conversion", tags = {"Document conversion"})
@Slf4j
public class DocumentConversionController {

    private DocumentConversionService documentConversionService;
    private DocumentManagementService documentManagementService;

    @ApiOperation(value = "Deletes document from the evidence management.", tags = {"Document Generation"})
    @ApiResponses({
        @ApiResponse(code = 204, message = "Document was deleted successfully and stored in the"
            + " evidence management. Returns the url to the stored document.", response = String.class),
        @ApiResponse(code = 400, message = "Returned when input parameters are invalid", response = String.class),
        @ApiResponse(code = 503, message = "Returned Evidence Management Client Api "
            + "cannot be reached", response = String.class),
        @ApiResponse(code = 500, message = "Returned when there is an unknown server error",
            response = String.class)
    })
    @PostMapping("/version/1/convert-to-pdf")
    public Document convertDocumentToPdf(
        @RequestHeader(value = "Authorization") String authorisationToken,
        @RequestBody Document document
    ) {
        log.info("document conversion service is null: {}", documentConversionService == null);
        byte[] convertedDocContent = documentConversionService.convertDocumentToPdf(document);
        String filename = documentConversionService.getConvertedFilename(document.getFileName());
        return storeDocument(convertedDocContent, filename, authorisationToken);
    }

    private Document storeDocument(byte[] source, String filename, String authorisationToken) {
        return documentManagementService.storeDocument(source, filename, authorisationToken);
    }
}
