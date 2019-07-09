package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import uk.gov.hmcts.reform.finrem.documentgenerator.GeneratedDocumentInfo;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.FetchPrintDocsService;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.PDFStampingService;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Api(value = "Document Generation", tags = {"Document Generation"})
@Slf4j
public class ApprovedConsentOrderController {

    @Autowired
    private DocumentManagementService documentManagementService;
    @Autowired
    private FetchPrintDocsService fetchPrintDocsService;
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
    @PostMapping("/version/1/generateApprovedConsentOrder")
    public ResponseEntity<Object> bulkPrint(@RequestHeader(value = "Authorization", required = false)
                                                String authorizationToken, @RequestBody
                                            @ApiParam(value = "CallbackRequest", required = true)
                                                CallbackRequest callbackRequest) {
        log.info("Stamping requested ");

        final CaseDetails caseDetails = callbackRequest.getCaseDetails();

        final Map<String, GeneratedDocumentInfo> generatedDocumentInfoList =
            fetchPrintDocsService.getGeneratedDocuments(callbackRequest,authorizationToken);

        try {
            byte[] courSeal = pdfStampingService.getCourSeal();
            pdfStampingService.stampDocument(generatedDocumentInfoList.get("caseDetails").getBytes(), courSeal);
        } catch (final Exception e) {
            log.error("Respondent pack bulk print failed for case {}", caseDetails.getId(), e);
        }
        return status(HttpStatus.OK).build();
    }
}
