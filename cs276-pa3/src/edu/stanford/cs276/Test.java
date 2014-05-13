package edu.stanford.cs276;

import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.doc.TermFreqExtractor;
import edu.stanford.cs276.scorer.AScorer;
import edu.stanford.cs276.scorer.CosineSimilarityScorer;

import java.util.Arrays;

/**
 * Created by kavinyao on 5/12/14.
 */
public class Test {
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

        AScorer scorer = new CosineSimilarityScorer(LoadHandler.loadIDFs());
        double score = scorer.getSimScore(doc, query);
        System.out.println("score = " + score);
    }
}
