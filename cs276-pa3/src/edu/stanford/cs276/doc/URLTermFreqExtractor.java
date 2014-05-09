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
    public Map<String, Double> extractFrom(Document d, Query q) {
        List<String> urlWords = tokenize(d.url, "\\W+");
        return termFreqsFromField(urlWords, q);
    }
}