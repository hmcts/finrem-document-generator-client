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
import uk.gov.hmcts.reform.finrem.documentgenerator.model.Document;
import uk.gov.hmcts.reform.finrem.documentgenerator.model.DocumentRequest;
import uk.gov.hmcts.reform.finrem.documentgenerator.service.DocumentManagementService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Api(value = "Document Generation", tags = {"Document Generation"})
@Slf4j
public class DocumentController {

    private final DocumentManagementService documentManagementService;

    @ApiOperation(value = "Generate PDF document based on the supplied template name and placeholder texts and saves "
            + "it in the evidence management.", tags = {"Document Generation"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "PDF was generated successfully and stored in the evidence management."
                    + " Returns the stored document information.", response = String.class),
            @ApiResponse(code = 400, message = "Returned when input parameters are invalid or template not found",
                    response = String.class),
            @ApiResponse(code = 503, message = "Returned when the PDF Service or Evidence Management Client Api "
                    + "cannot be reached", response = String.class),
            @ApiResponse(code = 500, message = "Returned when there is an unknown server error",
                    response = String.class)
        })
    @PostMapping("/version/1/generate-pdf")
    public Document generatePdf(@RequestHeader(value = "Authorization", required = false)
                                                     String authorizationToken, @RequestBody @Valid
        @ApiParam(value = "JSON object containing the templateName and the placeholder text map", required = true)
                                    DocumentRequest templateData) {
        log.info("Document generation requested with templateName [{}], placeholders map [{}]",
                    templateData.getTemplate(), templateData.getValues());

        return documentManagementService.storeDocument(templateData.getTemplate(), templateData.getFileName(),
            templateData.getValues(),
            authorizationToken);
    }
}
