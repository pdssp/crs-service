package com.geomatys.crsservice.rest;

import com.geomatys.crsservice.service.CrsOperationService;
import java.time.OffsetDateTime;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crs/operation")
public class CRSOperationController {

    private final CrsOperationService service;

    public CRSOperationController(CrsOperationService service) {
        this.service = service;
    }

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<Resource> getOperation(@RequestParam String source,
                                 @RequestParam String target,
                                 @RequestParam String format,
                                 @RequestParam(required = false) double[] aoi,
                                 @RequestParam(required = false) OffsetDateTime time) {
        return getOperation(new CrsOperationService.Specification(source, target, format, aoi, time));
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Resource> getOperation(@RequestBody CrsOperationService.Specification parameters) {
        var result = service.getOperation(parameters);
        return ResponseEntity.ok()
                .contentType(result.contentType())
                .body(result.sourceCode());
    }
}
