package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/9/14.
 */
public class AnchorTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Double> extractFrom(Document d, Query q) {
        if (d.anchors == null) {
            return EMPTY_MAP;
        }

        List<String> anchorWords = FieldProcessor.splitAnchors(d);

        return termFreqsFromField(anchorWords, q);
    }
}
