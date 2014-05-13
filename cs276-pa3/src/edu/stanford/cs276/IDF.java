package edu.stanford.cs276;

import edu.stanford.cs276.util.MapUtility;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by kavinyao on 5/7/14.
 */
public class IDF implements Serializable {
    private Map<String, Integer> termDocCounts;
    private int totalDocCount;

    public IDF(Map<String, Integer> termDocCounts, int totalDocCount) {
        this.termDocCounts = termDocCounts;
        this.totalDocCount = totalDocCount;
    }

    /**
     * Get Laplace-smoothed IDF value.
     * @param term
     * @return
     */
    public double getValue(String term) {
        int count = MapUtility.getWithFallback(termDocCounts, term, 0);
        return Math.log(1.0 * (totalDocCount + 1) / (count + 1));
    }
}
