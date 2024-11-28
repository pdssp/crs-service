package com.geomatys.crsservice.rest;

import com.geomatys.crsservice.service.CrsOperationService;
import java.time.OffsetDateTime;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crs")
public class CRSOperationController {

    private final CrsOperationService service;

    public CRSOperationController(CrsOperationService service) {
        this.service = service;
    }

    @CrossOrigin
    @RequestMapping(path = "define", method = RequestMethod.GET)
    public ResponseEntity<Resource> getCRS(
                                 @RequestParam String source,
                                 @RequestParam(required = false, defaultValue = "false") boolean longitudeFirst,
                                 @RequestParam String format) {
        return getCRS(new CrsOperationService.CRSParameters(source, longitudeFirst, format));
    }

    @CrossOrigin
    @RequestMapping(path = "define", method = RequestMethod.POST)
    public ResponseEntity<Resource> getCRS(@RequestBody CrsOperationService.CRSParameters parameters) {
        var result = service.getCRS(parameters);
        return ResponseEntity.ok()
                .contentType(result.contentType())
                .body(result.sourceCode());
    }

    @CrossOrigin
    @RequestMapping(path = "operation", method = RequestMethod.GET)
    public ResponseEntity<Resource> getOperation(
                                 @RequestParam String source,
                                 @RequestParam(required = false, defaultValue = "false") boolean sourceLongitudeFirst,
                                 @RequestParam String target,
                                 @RequestParam(required = false, defaultValue = "false") boolean targetLongitudeFirst,
                                 @RequestParam String format,
                                 @RequestParam(required = false) double[] aoi,
                                 @RequestParam(required = false) OffsetDateTime time) {
        return getOperation(new CrsOperationService.OperationParameters(source, sourceLongitudeFirst, target, targetLongitudeFirst, format, aoi, time));
    }

    @CrossOrigin
    @RequestMapping(path = "operation", method = RequestMethod.POST)
    public ResponseEntity<Resource> getOperation(@RequestBody CrsOperationService.OperationParameters parameters) {
        var result = service.getOperation(parameters);
        return ResponseEntity.ok()
                .contentType(result.contentType())
                .body(result.sourceCode());
    }
}
