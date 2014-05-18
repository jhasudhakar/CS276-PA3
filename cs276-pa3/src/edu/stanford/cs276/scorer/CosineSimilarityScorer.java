package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.util.MapUtility;

import java.util.HashMap;
import java.util.Map;

import static edu.stanford.cs276.util.ConfigLoader.setParameters;

public class CosineSimilarityScorer extends AScorer
{
    private static double SMOOTH_BODY_LENGTH = 500;
    private static Map<DocField, Double> C;
    final private static String CONFIG = "cosine.config";

    static {
        C = new HashMap<>();
        C.put(DocField.anchor, 1.0);
        C.put(DocField.body, 3.19);
        C.put(DocField.header, 4.40);
        C.put(DocField.title, 4.35);
        C.put(DocField.url, 4.30);
    }

    public CosineSimilarityScorer(IDF idfs) {
        super(idfs);

        setParameters(this, CONFIG);
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

    private double getNetScore(Map<DocField, Map<String, Double>> tfs, Query q,
                              Map<String, Double> tfQuery, Document d) {
        double score = 0.0;

        for (DocField docField : DocField.values()) {
            score += C.get(docField) * dotProduct(tfQuery, tfs.get(docField));
        }

        //System.out.println(q);
        //System.out.println(d);
        //System.out.println("Score=" + score + "\n");
        return score;
    }


    public void normalizeTFs(Map<DocField, Map<String, Double>> tfs, Document d, Query q) {
        for (Map<String, Double> tf : tfs.values()) {
            // lengthNormalize(sublinear(tf), d, q);
            lengthNormalize(tf, d, q);
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
