package edu.stanford.cs276.tune;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kavinyao on 5/16/14.
 */
public class BM25TuningConfig implements TuningConfig {
    private Map<String, double[]> paramRanges;

    protected BM25TuningConfig() {
        paramRanges = new HashMap<>();

        paramRanges.put("Bf#anchor", new double[]{0.0, 1.1});
        paramRanges.put("Bf#body", new double[]{0.2, 1.1});
        paramRanges.put("Bf#header", new double[]{0.2, 1.1});
        paramRanges.put("Bf#title", new double[]{0.2, 1.2});
        paramRanges.put("Bf#url", new double[]{0.3, 1.6});

        paramRanges.put("Wf#anchor", new double[]{1.0, 6.0});
        paramRanges.put("Wf#body", new double[]{1.0, 6.0});
        paramRanges.put("Wf#header", new double[]{1.0, 6.0});
        paramRanges.put("Wf#title", new double[]{1.0, 7.0});
        paramRanges.put("Wf#url", new double[]{1.0, 6.0});

        paramRanges.put("K1", new double[]{30.0, 50.0});
        paramRanges.put("lambda", new double[]{1.0, 5.0});
        paramRanges.put("lambdaPrime", new double[]{1.0, 6.0});
    }

    @Override
    public Map<String, double[]> getParamRanges() {
        return paramRanges;
    }

    @Override
    public double getNDCGThreshold() {
        return 0.885;
    }

    @Override
    public double getNDCGThresholdEx() {
        return 0.888;
    }

    @Override
    public String getResultFormatString() {
        return "bm25-good-config-%d.ser";
    }
}
