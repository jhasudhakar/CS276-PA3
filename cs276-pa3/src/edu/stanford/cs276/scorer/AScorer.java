package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.doc.TermFreqExtractor;
import edu.stanford.cs276.util.MapUtility;

import java.util.HashMap;
import java.util.Map;

public abstract class AScorer 
{
    protected IDF idfs;
    public AScorer(IDF idfs)
    {
        this.idfs = idfs;
    }

    // scores each document for each query
    public abstract double getSimScore(Document d, Query q);

    // handle the query vector
    public Map<String, Double> getQueryFreqs(Query q)
    {
        // get term frequency
        Map<String, Integer> counts = MapUtility.count(q.getQueryWords());

        // use IDF
        Map<String, Double> tfQuery = new HashMap<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String term = entry.getKey();
            tfQuery.put(term, 1.0 * counts.get(term) * idfs.getValue(term));
        }

        return tfQuery;
    }

    /**
     * Creates the various kinds of term frequencies (url, title, body, header, and anchor).
     * The implementation will be shared by subclasses.
     * @param d
     * @param q
     * @return
     */
    public Map<DocField, Map<String, Double>> getDocTermFreqs(Document d, Query q) {
        // map from tf type -> queryWord -> score
        Map<DocField, Map<String, Double>> tfs = new HashMap<>();

        for (DocField docField : DocField.values()) {
            tfs.put(docField, TermFreqExtractor.getExtractor(docField).extractFrom(d, q));
        }

        return tfs;
    }

    /**
     * Optimized dot product.
     * @param sv small vector
     * @param lv large vector
     * @return
     */
    private static double optimizedDotProduct(Map<String, Double> sv, Map<String, Double> lv) {
        return sv.entrySet()
                .stream()
                .mapToDouble(ev -> ev.getValue() * MapUtility.getWithFallback(lv, ev.getKey(), 0.0))
                .sum();
    }

    /**
     * Compute dot product of two sparse vectors.
     * @param v1 sparse vector
     * @param v2 sparse vector
     * @return
     */
    public double dotProduct(Map<String, Double> v1, Map<String, Double> v2) {
        // make sure v1 is smaller so that minimal computation is needed
        if (v1.size() > v2.size()) {
            return optimizedDotProduct(v2, v1);
        } else {
            return optimizedDotProduct(v1, v2);
        }
    }
}
