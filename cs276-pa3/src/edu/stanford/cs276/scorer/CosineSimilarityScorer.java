package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.util.MapUtility;

import java.util.HashMap;
import java.util.Map;

public class CosineSimilarityScorer extends AScorer
{
    private static double SMOOTH_BODY_LENGTH = 500;
    private static Map<DocField, Double> fieldWeights;

    static {
        fieldWeights = new HashMap<>();
        fieldWeights.put(DocField.url, 1.0);
        fieldWeights.put(DocField.title, 1.0);
        fieldWeights.put(DocField.header, 1.0);
        fieldWeights.put(DocField.body, 1.0);
        fieldWeights.put(DocField.anchor, 1.0);
    }

    public CosineSimilarityScorer(IDF idfs) {
        super(idfs);
    }

    /**
     * Normalize by body length.
     * @param termFreqs
     * @param d
     * @param q
     * @return
     */
    private Map<String, Double> lengthNormalize(Map<String, Double> termFreqs, Document d, Query q) {
        double smoothedBodyLength = d.getBodyLength() + SMOOTH_BODY_LENGTH;
        return MapUtility.iMap(termFreqs, f -> f / smoothedBodyLength);
    }

    private Map<String, Double> sublinear(Map<String, Double> vals) {
        return MapUtility.iMap(vals, val -> val == 0.0 ? 0.0 : 1 + Math.log(val));
    }

    public double getNetScore(Map<DocField, Map<String, Double>> tfs, Query q,
                              Map<String, Double> tfQuery, Document d) {
        double score = 0.0;

        for (DocField docField : DocField.values()) {
            score += dotProduct(tfQuery, tfs.get(docField));
        }

        return score;
    }


    public void normalizeTFs(Map<DocField, Map<String, Double>> tfs, Document d, Query q) {
        for (Map<String, Double> tf : tfs.values()) {
            lengthNormalize(sublinear(tf), d, q);
        }
    }

    @Override
    public double getSimScore(Document d, Query q) {

        Map<DocField, Map<String, Double>> tfs = getRawDocTermFreqs(d, q);

        normalizeTFs(tfs, d, q);

        Map<String, Double> tfQuery = getQueryFreqs(q);

        return getNetScore(tfs,q,tfQuery,d);
    }
}
