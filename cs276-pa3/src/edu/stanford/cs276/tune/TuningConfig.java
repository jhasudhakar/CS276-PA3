package edu.stanford.cs276.tune;

import java.util.Map;

/**
 * Created by kavinyao on 5/16/14.
 */
public interface TuningConfig {
    /**
     * Get ranges of parameters to generate parameters from.
     * @return
     */
    Map<String, double[]> getParamRanges();

    /**
     * Specify a good enough NDCG threshold.
     * @return
     */
    double getNDCGThreshold();

    /**
     * Specify an excellent NDCG threshold.
     * @return
     */
    double getNDCGThresholdEx();

    /**
     * Return a format string with a single %d format specifier.
     * @return
     */
    String getResultFormatString();
}
