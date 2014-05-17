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

        paramRanges.put("C#anchor", new double[]{0.0, 5.0});
        paramRanges.put("C#body", new double[]{0.0, 5.0});
        paramRanges.put("C#header", new double[]{0.0, 5.0});
        paramRanges.put("C#title", new double[]{0.0, 5.0});
        paramRanges.put("C#url", new double[]{0.0, 5.0});
    }

    @Override
    public Map<String, double[]> getParamRanges() {
        return paramRanges;
    }

    @Override
    public double getNDCGThreshold() {
        return 0.865;
    }

    @Override
    public double getNDCGThresholdEx() {
        return 0.8675;
    }

    @Override
    public String getResultFormatString() {
        return "cosine-good-config-%d.ser";
    }
}
