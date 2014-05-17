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

        paramRanges.put("C#anchor", new double[]{1.0, 1.0});
        paramRanges.put("C#body", new double[]{2.5, 3.5});
        paramRanges.put("C#header", new double[]{4.0, 4.5});
        paramRanges.put("C#title", new double[]{4.0, 4.5});
        paramRanges.put("C#url", new double[]{4.0, 4.5});
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
        return 0.8675;
    }

    @Override
    public String getResultFormatString() {
        return "cosine-good-config-%d.ser";
    }
}
