package edu.stanford.cs276.tune;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kavinyao on 5/17/14.
 */
public class SWTuningConfig implements TuningConfig {
    @Override
    public Map<String, double[]> getParamRanges() {
        Map<String, double[]> paramRanges = new HashMap<>();
        paramRanges.put("B", new double[]{1.0, 10.0});
        return paramRanges;
    }

    @Override
    public double getNDCGThreshold() {
        return 0.851;
    }

    @Override
    public double getNDCGThresholdEx() {
        return 0.8525;
    }

    @Override
    public String getResultFormatString() {
        return "sw-good-config-%d.ser";
    }
}
