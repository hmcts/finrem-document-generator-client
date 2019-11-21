package uk.gov.hmcts.reform.finrem.documentgenerator.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentValidationResponse;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentValidationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
public class DocumentValidationController {

    @Autowired
    private DocumentValidationService service;

    @GetMapping(path = "/file-upload-check", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Checks the file type and returns error.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Callback was processed successFully.",
            response = AboutToStartOrSubmitCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")})
    public DocumentValidationResponse checkUploadedFileType(
        @RequestHeader(value = "Authorization") String authorisationToken,
        @RequestParam(value = "fileBinaryUrl") String fileBinaryUrl) {

        log.info("Received request for checkUploadedFileType. Auth token: {}, binaryFileUrl: {}",
            authorisationToken, fileBinaryUrl);
        return response(fileBinaryUrl);
    }

    private DocumentValidationResponse response(String fileBinaryUrl) {
        return service.validateFileType(fileBinaryUrl);
    }
}
