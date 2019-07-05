package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.divorce.documentgenerator.service.DocumentManagementService;
import uk.gov.hmcts.reform.divorce.documentgenerator.service.PDFStampingService;

@RestController
@Api(value = "Document Generation", tags = {"Document Generation"})
@Slf4j
public class StampingController {

    @Autowired
    private DocumentManagementService documentManagementService;

    private PDFStampingService stampingService;

    @ApiOperation(value = "Bulk Print", tags = {"Bulk print"})
    @ApiResponses( {
    @ApiResponse(code = 200, message = "Documents sent for bulk printing."
            + " Returns the stored document information.", response = String.class),
    @ApiResponse(code = 400, message = "Returned when input parameters are invalid ",
            response = String.class),
    @ApiResponse(code = 503, message = "Returned when the Bulk print Service or Evidence Management Client Api "
            + "cannot be reached", response = String.class),
    @ApiResponse(code = 500, message = "Returned when there is an unknown server error",
            response = String.class)
        })
    @PostMapping("/version/1/generateApprovedConsentOrder")
    public ResponseEntity<Object> bulkPrint(@RequestHeader(value = "Authorization", required = false)
                                                String authorizationToken, @RequestBody
                                            @ApiParam(value = "CallbackRequest", required = true)
                                                CallbackRequest callbackRequest) {
        log.info("Generate Approved Consent Order + Stamping requested ");

        final CaseDetails caseDetails = callbackRequest.getCaseDetails();

        try {
            stampingService.send(caseDetails.getId().toString(), LETTER_TYPE_RESPONDENT_PACK,
                asList(generatedDocumentInfoList.get(0)));
        } catch (final Exception e) {
            log.error("Respondent pack bulk print failed for case {}", caseDetails.getId(), e);
        }
        return status(HttpStatus.OK).build();
    }
