package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

import static org.springframework.http.ResponseEntity.status;

@RestController
@Api(value = "Document Generation", tags = {"Document Generation"})
@Slf4j
public class DeleteDocumentController {

    @Autowired
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
    @DeleteMapping("/version/1/delete-pdf-document")
    public ResponseEntity<Object> deleteDocument(@RequestHeader(value = "Authorization", required = false)
                                                     String authorizationToken,
                                                 @RequestParam(value = "fileUrl") String fileUrl) {
        log.info("Document to be deleted {}", fileUrl);
        documentManagementService.deleteDocument(fileUrl, authorizationToken);
        return status(HttpStatus.NO_CONTENT).build();
    }
}
