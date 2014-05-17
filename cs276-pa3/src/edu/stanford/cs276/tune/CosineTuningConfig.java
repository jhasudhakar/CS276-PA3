package edu.stanford.cs276.tune;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kavinyao on 5/16/14.
 */
public class CosineTuningConfig implements TuningConfig {
    private Map<String, double[]> paramRanges;

    protected CosineTuningConfig() {
        paramRanges = new HashMap<>();

        paramRanges.put("fieldWeights#anchor", new double[]{0.0, 5.0});
        paramRanges.put("fieldWeights#body", new double[]{0.0, 5.0});
        paramRanges.put("fieldWeights#header", new double[]{0.0, 5.0});
        paramRanges.put("fieldWeights#title", new double[]{0.0, 5.0});
        paramRanges.put("fieldWeights#url", new double[]{0.0, 5.0});
    }

    @Override
    public Map<String, double[]> getParamRanges() {
        return paramRanges;
    }

    @Override
    public double getNDCGThreshold() {
        return 0.85;
    }

    @Override
    public double getNDCGThresholdEx() {
        return 0.87;
    }

    @Override
    public String getResultFormatString() {
        return "cosine-good-config-%d.ser";
    }
}
