package edu.stanford.cs276;

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
     * Compute dot product of two sparse vectors.
     * As qv size is usually smaller than dv, the order matters.
     * @param qv the query vector
     * @param dv the document vector
     * @return
     */
    public double dotProduct(Map<String, Double> qv, Map<String, Double> dv) {
        double result = 0.0;

        for (Map.Entry<String, Double> ev : qv.entrySet()) {
            result += ev.getValue() * MapUtility.getWithFallback(dv, ev.getKey(), 0.0);
        }

        return result;
    }
}
