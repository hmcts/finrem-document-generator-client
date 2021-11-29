package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.BulkPrintRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.BulkPrintDocumentService;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.BulkPrintService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Api(value = "Bulk print controller", tags = {"Bulk print controller"})
@Slf4j
public class BulkPrintController {

    private final BulkPrintService bulkPrintService;
    private final BulkPrintDocumentService bulkPrintDocumentService;

    @ApiOperation(value = "Bulk Print documents", tags = {"Bulk print documents"})
    @ApiResponses({
        @ApiResponse(code = 200, message = "Documents sent for bulk printing.", response = String.class),
        @ApiResponse(code = 400, message = "Returned when input parameters are invalid ", response = String.class),
        @ApiResponse(code = 503, message = "Returned when the Bulk print Service or Evidence Management Client Api "
            + "cannot be reached", response = String.class),
        @ApiResponse(code = 500, message = "Returned when there is an unknown server error", response = String.class)
    })
    @PostMapping("/version/1/bulk-print")
    public UUID bulkPrint(
        @RequestHeader(value = "Authorization") String authorizationToken,
        @RequestBody @ApiParam(value = "BulkPrintRequest", required = true) BulkPrintRequest bulkPrintRequest
    ) {
        log.info("Bulk print request is being processed for case {}", bulkPrintRequest.getCaseId());
        try {
            final List<byte[]> documents = bulkPrintDocumentService.downloadDocuments(bulkPrintRequest, authorizationToken);
            return bulkPrintService.send(bulkPrintRequest.getCaseId(), bulkPrintRequest.getLetterType(), documents);
        } catch (final Exception e) {
            log.error("Bulk print failed for case {}", bulkPrintRequest.getCaseId(), e);
            throw new RuntimeException(e);
        }
    }
}
