package edu.stanford.cs276;

import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.util.MapUtility;

import java.util.Map;

public class CosineSimilarityScorer extends AScorer
{
    private static double EXT_BODY_LENGTH = 500;
	public CosineSimilarityScorer(IDF idfs)
	{
		super(idfs);
	}
	
	///////////////weights///////////////////////////
    double urlweight = -1;
    double titleweight  = -1;
    double bodyweight = -1;
    double headerweight = -1;
    double anchorweight = -1;
    
    double smoothingBodyLength = -1;

    /**
     * Normalize by body length.
     * @param termFreqs
     * @param d
     * @param q
     * @return
     */
    private Map<String, Double> lengthNormalize(Map<String, Double> termFreqs, Document d, Query q) {
        double smoothedBodyLength = d.bodyLength + EXT_BODY_LENGTH;

        for (Map.Entry<String, Double> e : termFreqs.entrySet()) {
            termFreqs.put(e.getKey(), e.getValue() / smoothedBodyLength);
        }

        return termFreqs;
    }

    private Map<String, Double> sublinear(Map<String, Double> vals) {
        return MapUtility.iMap(vals, val -> val == 0.0 ? 0.0 : 1 + Math.log(val));
    }

	public double getNetScore(Map<DocField, Map<String, Double>> tfs, Query q,
                              Map<String, Double> tfQuery, Document d)
	{
		double score = 0.0;
		
		/*
		 * @//TODO : Your code here
		 */
		
		return score;
	}

	
	public void normalizeTFs(Map<DocField, Map<String, Double>> tfs, Document d, Query q)
	{
        for (Map<String, Double> tf : tfs.values()) {
            lengthNormalize(sublinear(tf), d, q);
        }
    }

	@Override
	public double getSimScore(Document d, Query q) 
	{
		
		Map<DocField, Map<String, Double>> tfs = getDocTermFreqs(d, q);
		
		normalizeTFs(tfs, d, q);
		
		Map<String, Double> tfQuery = getQueryFreqs(q);
		
        return getNetScore(tfs,q,tfQuery,d);
	}

	
	
	
	
}
