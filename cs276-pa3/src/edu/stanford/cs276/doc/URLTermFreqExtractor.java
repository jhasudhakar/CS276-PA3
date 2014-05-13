package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/8/14.
 */
public class URLTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Integer> extractFrom(Document d, Query q) {
        List<String> urlWords = d.getFieldTokens(DocField.url);
        return termFreqsFromField(urlWords, q);
    }
}
