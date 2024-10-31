package com.geomatys.crsservice.service;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.PathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

// TODO: replace this with a real implementation
@Service
public class HelloWordService implements CrsOperationService {

    @Override
    public SourceCode getOperation(Specification specification) throws IllegalArgumentException, UnsupportedOperationException {
        return new SourceCode(
                MediaType.parseMediaType("application/javascript"),
                new ByteArrayResource("console.log(\"Hello\");".getBytes(StandardCharsets.UTF_8))
                // Use the following construct to use an existing file as result instead
                //new org.springframework.core.io.PathResource("/tmp/my-op.js")
        );
    }
}
