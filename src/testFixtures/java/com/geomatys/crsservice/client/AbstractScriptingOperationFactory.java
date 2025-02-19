package com.geomatys.crsservice.client;

import com.geomatys.crsservice.service.DefaultCrsOperationService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.sis.referencing.factory.InvalidGeodeticParameterException;
import org.apache.sis.referencing.operation.AbstractCoordinateOperation;
import org.apache.sis.referencing.operation.DefaultConversion;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.python.jsr223.PyScriptEngine;

public abstract class AbstractScriptingOperationFactory implements ScriptingCoordinateOperationFactory {

    abstract String getOperationCode(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2, final String format);

    @Override
    public CoordinateOperation createOperation(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2, final String format) throws FactoryException {
        try {
            final String code = getOperationCode(crs1, crs2, format);
            final ScriptEngineManager manager = new ScriptEngineManager();
            final ScriptEngine engine;
            final String type;
            if (DefaultCrsOperationService.FORMAT_JAVASCRIPT.equals(format)) {
                engine = manager.getEngineByName("js");
                engine.eval("const operationClass = (" + code + ");\noperation = new operationClass();");
                type = "JavaScript";
            } else if (DefaultCrsOperationService.FORMAT_PYTHON.equals(format)) {
                engine = manager.getEngineByName("python");
                engine.eval(code);
                type = "Python";
            } else {
                throw new FactoryException("Format not supported yet : " + format);
            }
            final ScriptMathTransform trs = new ScriptMathTransform(crs1.getCoordinateSystem().getDimension(), crs2.getCoordinateSystem().getDimension(), (Invocable) engine);

            return new AbstractCoordinateOperation(Map.of("name", type + " operation"), crs1, crs2, null, trs);
        } catch (RuntimeException | ScriptException ex) {
            throw new FactoryException("Cannot evaluate conversion script", ex);
        }
    }

    @Override
    public CoordinateOperation createOperation(CoordinateReferenceSystem crs, CoordinateReferenceSystem crs1, OperationMethod om) throws FactoryException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public CoordinateOperation createConcatenatedOperation(Map<String, ?> map, CoordinateOperation... cos) throws FactoryException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Conversion createDefiningConversion(Map<String, ?> map, OperationMethod om, ParameterValueGroup pvg) throws FactoryException {
        final Conversion conversion;
        try {
            conversion = new DefaultConversion(map, om, null, pvg);
        } catch (IllegalArgumentException exception) {
            throw new InvalidGeodeticParameterException(exception.getLocalizedMessage(), exception);
        }
        // We do no invoke unique(conversion) because defining conversions are usually short-lived objects.
        return conversion;
    }

    private static final class ScriptMathTransform extends AbstractMathTransform {

        private final int sourceDim;
        private final int targetDim;
        private final Invocable engine;

        private ScriptMathTransform(int sourceDim, int targetDim, Invocable engine) {
            this.sourceDim = sourceDim;
            this.targetDim = targetDim;
            this.engine = engine;
        }

        @Override
        public int getSourceDimensions() {
            return sourceDim;
        }

        @Override
        public int getTargetDimensions() {
            return targetDim;
        }

        @Override
        public Matrix transform(double[] src, int so, double[] dst, int doffset, boolean derivate) throws TransformException {
            if (derivate) {
                throw new TransformException("Derivate not supported");
            }

            try {
                final Object[] array = new Object[sourceDim];
                for (int i = 0; i < sourceDim; i++) {
                    array[i] = src[so + i];
                }
                final List<? extends Number> result;
                if (engine instanceof PyScriptEngine) {
                    result = (List) ((PyScriptEngine) engine).eval("Operation().transform(" + Arrays.toString(array) + ")");
                } else {
                    final Object jsOperation = ((ScriptEngine) engine).eval("operation");
                    result = (List) engine.invokeMethod(jsOperation, "transform", ProxyArray.fromArray(array));
                }
                for (int i = 0; i < targetDim; i++) {
                    dst[doffset + i] = result.get(i).doubleValue();
                }
            } catch (ScriptException | NoSuchMethodException ex) {
                throw new TransformException(ex.getMessage(), ex);
            }
            return null;
        }
    }
}
