package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.util.MapUtility;

import java.util.*;

/**
 * Extract raw term frequencies from a certain field.
 * Created by kavinyao on 5/8/14.
 */
public abstract class TermFreqExtractor {
    private static Map<DocField, TermFreqExtractor> extractors;
    protected static Map<String, Double> EMPTY = new HashMap<>();

    static {
        // initialize known field vectors
        extractors = new HashMap<>();
        extractors.put(DocField.url, new URLTermFreqExtractor());
        extractors.put(DocField.title, new TitleTermFreqExtractor());
        extractors.put(DocField.header, new TitleTermFreqExtractor());
        extractors.put(DocField.body, new BodyTermFreqExtractor());
        extractors.put(DocField.anchor, new AnchorTermFreqExtractor());
    }

    public static TermFreqExtractor getExtractor(DocField df) {
        return extractors.get(df);
    }

    /**
     * Compute raw term frequencies.
     * @param d
     * @param q
     * @return
     */
    public abstract Map<String, Double> extractFrom(Document d, Query q);

    // Helper methods

    /**
     * Count fieldWords and filter out query terms.
     * @param fieldWords
     * @param q
     * @return
     */
    protected Map<String, Double> termFreqsFromField(List<String> fieldWords, Query q) {
        Map<String, Integer> counts = MapUtility.count(fieldWords);
        Map<String, Double> termFreqs = new HashMap<>();
        for (String qw : q.getQueryWords()) {
            double tf = MapUtility.getWithFallback(counts, qw, 0);
            termFreqs.put(qw, tf);
        }

        return termFreqs;
    }

    protected List<String> tokenize(String s, String sep) {
        return Arrays.asList(s.split(sep));
    }
}
