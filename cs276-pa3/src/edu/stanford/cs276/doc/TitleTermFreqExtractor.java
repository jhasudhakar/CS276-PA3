package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/9/14.
 */
public class TitleTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Integer> getFieldTermFreqs(Document d) {
        List<String> titleWords = d.getFieldTokens(DocField.title);
        return termFreqsFromField(titleWords);
    }
}
