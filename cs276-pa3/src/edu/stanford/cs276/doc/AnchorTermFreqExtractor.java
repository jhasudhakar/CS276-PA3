package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/9/14.
 */
public class AnchorTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Double> extractFrom(Document d, Query q) {
        if (d.anchors == null) {
            return EMPTY;
        }

        List<String> anchorWords = new ArrayList<>();

        for (String anchorText : d.anchors.keySet()) {
            anchorWords.addAll(tokenize(anchorText.toLowerCase(), "\\s+"));
        }

        return termFreqsFromField(anchorWords, q);
    }
}
