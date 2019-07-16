package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.PDFStampingService;

@RestController
@Api(value = "Document Generation", tags = {"Document Generation"})
@Slf4j
public class StampDocumentController {

    @Autowired
    private PDFStampingService pdfStampingService;

    @ApiOperation(value = "Consent Order Approved", tags = {"Consent Order Approved"})
    @ApiResponses( {
    @ApiResponse(code = 200, message = "Documents sent for stamping"
            + " Returns the stored document information.", response = String.class),
    @ApiResponse(code = 400, message = "Returned when input parameters are invalid ",
            response = String.class),
    @ApiResponse(code = 503, message = "Returned when the Stamping Service or Evidence Management Client Api "
            + "cannot be reached", response = String.class),
    @ApiResponse(code = 500, message = "Returned when there is an unknown server error",
            response = String.class)
        })
    @PostMapping("/version/1/stamp-document")
    public Document stampDocument(@RequestHeader(value = "Authorization", required = false)
                                                String authorizationToken, @RequestBody
                                            @ApiParam(value = "Document to be stamped", required = true)
                                                    Document document) {
        log.info("Stamping requested for document : {}, auth token : {}", document, authorizationToken);
        return pdfStampingService.stampDocument(document, authorizationToken, false);
    }
}
