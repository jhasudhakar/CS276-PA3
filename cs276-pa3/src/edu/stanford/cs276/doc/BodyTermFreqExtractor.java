package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/9/14.
 */
public class BodyTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Double> extractFrom(Document d, Query q) {
        if (d.bodyHits == null) {
            return EMPTY;
        }

        Map<String, Double> bodyTF = new HashMap<>();

        System.err.println(d);
        for (Map.Entry<String, List<Integer>> et : d.bodyHits.entrySet()) {
            bodyTF.put(et.getKey(), (double) et.getValue().size());
        }

        return bodyTF;
    }
}
