package com.geomatys.crsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.privy.ExportableTransform;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.util.FactoryException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@Service
public class DefaultCrsOperationService implements CrsOperationService {

    private static final String FORMAT_JAVASCRIPT = "text/javascript";
    private static final String FORMAT_PYTHON = "text/x-python";
    private static final String FORMAT_JSON = "application/json";
    private static final String FORMAT_WKT ="application/wkt";

    @Override
    public SourceCode getCRS(CRSParameters request) throws IllegalArgumentException, UnsupportedOperationException {

        final CoordinateReferenceSystem crs;
        try {
            crs = parseCRS(request.source(), request.longitudeFirst());
        } catch (FactoryException ex) {
            throw new IllegalArgumentException("Source CRS unsupported : " + request.source(), ex);
        }

        final String format = request.format();
        if (FORMAT_JSON.equals(format)) {
            final Envelope domainOfValidity = CRS.getDomainOfValidity(crs);
            final Map<String,Object> map = new LinkedHashMap<>();
            map.put("code", request.source());
            map.put("domainOfValidity", new double[]{domainOfValidity.getMinimum(0), domainOfValidity.getMinimum(1), domainOfValidity.getMaximum(0), domainOfValidity.getMaximum(1)});
            map.put("units", crs.getCoordinateSystem().getAxis(0).getUnit().getSymbol());

            final List<String> axisDirection = new ArrayList();
            for (int i = 0; i < crs.getCoordinateSystem().getDimension(); i++) {
                axisDirection.add(crs.getCoordinateSystem().getAxis(i).getDirection().identifier());
            }
            map.put("axisDirection", axisDirection);

            final ObjectMapper mapper = new ObjectMapper();
            final String json;
            try {
                json = mapper.writeValueAsString(map);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }

            return new SourceCode(
                MediaType.parseMediaType(FORMAT_JSON + "; charset=utf-8"),
                new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8))
            );

        } else if (FORMAT_WKT.equals(format)) {
            throw new UnsupportedOperationException("TODO");
        } else {
            throw new IllegalArgumentException("Format not supported " + format);
        }

    }

    @Override
    public SourceCode getOperation(OperationParameters request) throws IllegalArgumentException, UnsupportedOperationException {

        final CoordinateReferenceSystem crs1;
        final CoordinateReferenceSystem crs2;
        try {
            crs1 = parseCRS(request.source(), request.sourceLongFirst());
        } catch (FactoryException ex) {
            throw new IllegalArgumentException("Source CRS unsupported : " + request.source(), ex);
        }
        try {
            crs2 = parseCRS(request.target(), request.targetLongFirst());
        } catch (FactoryException ex) {
            throw new IllegalArgumentException("Target CRS unsupported : " + request.target(), ex);
        }

        final double[] aoi = request.aoi();
        final GeographicBoundingBox ggb = (aoi == null) ? null : null;
        final CoordinateOperation operation;
        try {
            operation = CRS.findOperation(crs1, crs2, ggb);
        } catch (FactoryException ex) {
            throw new UnsupportedOperationException("No coordinate operation found between " + request.source() + " and " + request.target(), ex);
        }
        final MathTransform trs = operation.getMathTransform();
        MathTransform inverseTrs = null;
        try {
            inverseTrs = trs.inverse();
        } catch (NoninvertibleTransformException ex) {
            //no nothing
        }

        final double linearAccuracy = CRS.getLinearAccuracy(operation);
        final GeographicBoundingBox gbb = CRS.getGeographicBoundingBox(operation);
        final Envelope domainOfValidity = CRS.getDomainOfValidity(crs2);

        final String format = request.format();
        if (FORMAT_JAVASCRIPT.equals(format)) {
            final String code = toJavaScript(trs, inverseTrs, linearAccuracy, gbb, domainOfValidity, false);
            return new SourceCode(
                MediaType.parseMediaType(FORMAT_JAVASCRIPT + "; charset=utf-8"),
                new ByteArrayResource(code.getBytes(StandardCharsets.UTF_8))
            );
        } else if (FORMAT_PYTHON.equals(format)) {
            throw new UnsupportedOperationException("TODO");
        } else {
            throw new IllegalArgumentException("Format not supported " + format);
        }

    }

    private static CoordinateReferenceSystem parseCRS(String text, boolean longFirst) throws FactoryException {
        CoordinateReferenceSystem crs;
        try {
            crs = CRS.fromWKT(text);
        } catch (FactoryException ex) {
            try {
                return CRS.forCode(text);
            } catch (FactoryException ex1) {
                ex.addSuppressed(ex1);
                throw ex;
            }
        }
        if (longFirst) {
            crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.DISPLAY_ORIENTED);
        }
        return crs;
    }

    /**
     *
     * @param trs
     * @param invtrs
     * @param accuracy
     * @param operationGeographicBoundingBox
     * @param module true to generate a module
     * @return
     */
    private static String toJavaScript(MathTransform trs, MathTransform invtrs, double accuracy, GeographicBoundingBox operationGeographicBoundingBox, Envelope targetCrsDomainOfValidity, boolean module) {
        final StringBuilder sb = new StringBuilder();

        sb.append(
                "/* Code generated by Apache SIS. https://sis.apache.org \n" +
                " *\n" +
                " * Licensed to the Apache Software Foundation (ASF) under one or more\n" +
                " * contributor license agreements.  See the NOTICE file distributed with\n" +
                " * this work for additional information regarding copyright ownership.\n" +
                " * The ASF licenses this file to You under the Apache License, Version 2.0\n" +
                " * (the \"License\"); you may not use this file except in compliance with\n" +
                " * the License.  You may obtain a copy of the License at\n" +
                " *\n" +
                " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                " *\n" +
                " * Unless required by applicable law or agreed to in writing, software\n" +
                " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                " * See the License for the specific language governing permissions and\n" +
                " * limitations under the License.\n" +
                " */\n");

        sb.append(module ? "" : "operation = {\n");

        sb.append("/*\n * The valid geographic area for the given coordinate operation (as an array [west, south, east, north]), or undefined\n */\n");
        if (operationGeographicBoundingBox != null) {
            sb.append(module ? "const operationGeographicBoundingBox =" : "operationGeographicBoundingBox :");
            sb.append(" [")
                    .append(operationGeographicBoundingBox.getWestBoundLongitude()).append(", ")
                    .append(operationGeographicBoundingBox.getSouthBoundLatitude()).append(", ")
                    .append(operationGeographicBoundingBox.getEastBoundLongitude()).append(", ")
                    .append(operationGeographicBoundingBox.getNorthBoundLatitude())
                    .append("]");
            sb.append(module ? ";" : ",");
            sb.append("\n\n");
        } else {
            sb.append(module ? "const operationGeographicBoundingBox = undefined;" : "operationGeographicBoundingBox : undefined,");
            sb.append("\n\n");
        }

        sb.append("/*\n * The target coordinate reference system domain of validity, (as an array [minX, minY, maxX, maxY]), or undefined\n */\n");
        if (targetCrsDomainOfValidity != null) {
            sb.append(module ? "const domainOfValidity =" : "domainOfValidity :");
            sb.append(" [")
                    .append(targetCrsDomainOfValidity.getMinimum(0)).append(", ")
                    .append(targetCrsDomainOfValidity.getMinimum(1)).append(", ")
                    .append(targetCrsDomainOfValidity.getMaximum(0)).append(", ")
                    .append(targetCrsDomainOfValidity.getMaximum(1))
                    .append("]");
            sb.append(module ? ";" : ",");
            sb.append("\n\n");
        } else {
            sb.append(module ? "const domainOfValidity = undefined;" : "domainOfValidity : undefined,");
            sb.append("\n\n");
        }

        sb.append("/*\n * Positional accuracy estimation in metres for the given operation, or NaN if unknown.\n */\n");
        sb.append(module ? "const accuracy = " : "accuracy : ");
        sb.append(accuracy);
        sb.append(module ? ";" : ",");
        sb.append("\n\n");

        {
            final Map<String,String> decomposed = new LinkedHashMap<>();
            sb.append("/*\n * The mathematical formula to transform coordinates\n */\n");
            toJavaScript(decomposed,"transform", trs, new AtomicInteger());
            for (Entry<String,String> entry : decomposed.entrySet()) {
                final String functionName = entry.getKey();
                sb.append(module ? "function "+functionName : functionName+": function");
                sb.append(entry.getValue());
                sb.append(module ? "" : ",");
                sb.append("\n\n");
            }
        }

        {
            sb.append("/*\n * The mathematical formula to inverse transform coordinates, can be undefined.\n */\n");
            if (invtrs != null) {
                final Map<String,String> decomposed = new LinkedHashMap<>();
                toJavaScript(decomposed,"inverseTransform", invtrs, new AtomicInteger());
                for (Entry<String,String> entry : decomposed.entrySet()) {
                    final String functionName = entry.getKey();
                    sb.append(module ? "function "+functionName : functionName+": function");
                    sb.append(entry.getValue());
                    sb.append(module ? "" : ",");
                    sb.append("\n\n");
                }
            } else {
                sb.append("const inverseTransform = undefined;");
                sb.append(module ? "" : ",");
                sb.append("\n\n");
            }
        }

        sb.append(module ? "module.exports = { transform, inverseTransform, accuracy, areaOfValidity }\n" : "}");

        return sb.toString();
    }

    private static String toJavaScript(Map<String,String> fctMap, String functionName, MathTransform trs, AtomicInteger increment) {
        final int subi = increment.get();
        final String finalName = ((subi == 0) ? "" : "_") + functionName + ((subi == 0) ? "" : (""+subi));
        fctMap.put(finalName, ""); //placeholder to preserve function order

        final StringBuilder sb = new StringBuilder();
//        sb.append(module ? "function "+functionName : functionName+": function");
        sb.append("(src){\n");

        final List<MathTransform> steps = decompose(trs);

        if (steps.size() == 1) {
            final MathTransform step = steps.get(0);
            final int targetDimensions = step.getTargetDimensions();

            sb.append("\n\t// ").append(step.getClass().getSimpleName()).append("\n");
            sb.append("\tdst = new Array(").append(targetDimensions).append(");\n");

            if (step instanceof ExportableTransform exp) {
                final String code;
                try {
                    code = exp.toECMAScript().replaceAll("\n", "\n\t");
                    sb.append("\t");
                    sb.append(code);
                    sb.append('\n');
                } catch (UnsupportedOperationException ex) {
                    sb.append("\t// TODO unsupported + ").append(ex.getMessage()).append("\n");
                }
            } else {
                sb.append("\t// TODO " + step.getClass().getName() + "\n");
            }

        } else {
            //create a separate function for each step, to avoid possible variable name conflicts
            for (int i = 0, n = steps.size(); i < n; i++) {
                final MathTransform step = steps.get(i);
                sb.append("\n\t// STEP ").append(i+1).append(" : ").append(step.getClass().getSimpleName()).append("\n");
                increment.incrementAndGet();
                final String fctName = toJavaScript(fctMap, functionName, step, increment);
                //dst becomes src for next step
                sb.append("\tdst = src = this.").append(fctName).append("(src);\n");
            }
        }

        sb.append("\n\treturn dst;\n}");

        fctMap.put(finalName, sb.toString());
        return finalName;
    }

    private static List<MathTransform> decompose(MathTransform trs) {
        final List<MathTransform> steps = MathTransforms.getSteps(trs);

        if (steps.size() == 1) {
            return steps;
        }

        final List<MathTransform> decompose = new ArrayList<>();
        for (MathTransform step : steps) {
            decompose.addAll(decompose(step));
        }
        return decompose;
    }

}
