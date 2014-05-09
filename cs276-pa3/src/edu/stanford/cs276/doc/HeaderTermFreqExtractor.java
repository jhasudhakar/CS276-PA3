package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/9/14.
 */
public class HeaderTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Double> extractFrom(Document d, Query q) {
        List<String> headerWords = new ArrayList<>();

        for (String header : d.headers) {
            headerWords.addAll(tokenize(header.toLowerCase(), "\\s+"));
        }

        return termFreqsFromField(headerWords, q);
    }
}
