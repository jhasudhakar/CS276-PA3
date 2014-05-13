package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.util.MapUtility;

import java.util.*;

/**
 * Extract raw term frequencies from a certain field.
 * Created by kavinyao on 5/8/14.
 */
public abstract class TermFreqExtractor {
    private static Map<DocField, TermFreqExtractor> extractors;

    static {
        // initialize known field vectors
        extractors = new HashMap<>();
        extractors.put(DocField.url, new URLTermFreqExtractor());
        extractors.put(DocField.title, new TitleTermFreqExtractor());
        extractors.put(DocField.header, new HeaderTermFreqExtractor());
        extractors.put(DocField.body, new BodyTermFreqExtractor());
        extractors.put(DocField.anchor, new AnchorTermFreqExtractor());
    }

    public static TermFreqExtractor getExtractor(DocField df) {
        return extractors.get(df);
    }

    /**
     * Compute raw term frequencies.
     * @param d
     * @return
     */
    protected abstract Map<String, Integer> getFieldTermFreqs(Document d);

    public Map<String, Integer> getTermFreqs(Document d, Query q) {
        Map<String, Integer> counts = getFieldTermFreqs(d);
        return filterByQueryTerms(counts, q);
    }


    // Helper methods

    /**
     * Count fieldWords and filter out query terms.
     * @param fieldWords
     * @return
     */
    protected Map<String, Integer> termFreqsFromField(List<String> fieldWords) {
        if (fieldWords.size() == 0) {
            return Collections.emptyMap();
        }

        return MapUtility.count(fieldWords);
    }

    protected Map<String, Integer> filterByQueryTerms(Map<String, Integer> counts, Query q) {
        Map<String, Integer> termFreqs = new HashMap<>();

        for (String qw : q.getQueryWords()) {
            int tf = MapUtility.getWithFallback(counts, qw, 0);
            termFreqs.put(qw, tf);
        }

        return termFreqs;
    }

    // tiny test
    public static void main(String[] args) {
        Document doc = new Document("http://math.stanford.edu/");
        doc.setTitle("department of mathematics stanford university");

        doc.addHeader("Stanford Math Department");
        doc.addHeader("Latest publications in math");

        Integer[] hits1 = {23, 44, 92, 159, 165};
        doc.addBodyHits("stanford", Arrays.asList(hits1));
        Integer[] hits2 = {97, 118};
        doc.addBodyHits("2014", Arrays.asList(hits2));
        doc.setBodyLength(251);

        doc.addAnchor("http math stanford edu", 44);
        doc.addAnchor("stanford math department", 9);
        doc.end();

        Query query = new Query("2014 math requirements stanford");
        for (DocField docField : DocField.values()) {
            TermFreqExtractor ae = TermFreqExtractor.getExtractor(docField);
            System.out.println("Field " + docField + ": " + ae.getTermFreqs(doc, query));
        }
    }
}
