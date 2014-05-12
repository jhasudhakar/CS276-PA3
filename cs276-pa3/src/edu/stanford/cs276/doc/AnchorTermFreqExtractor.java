package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.util.MapUtility;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kavinyao on 5/9/14.
 */
public class AnchorTermFreqExtractor extends TermFreqExtractor {
    @Override
    public Map<String, Double> extractFrom(Document d, Query q) {
        Map<String, Integer> anchors = d.getAnchors();

        if (anchors.size() == 0) {
            return Collections.emptyMap();
        }

        Map<String, Double> counts = anchors.entrySet()
                .stream()
                .map(et -> {
                    List<String> tokens = FieldProcessor.splitField(et.getKey());
                    return MapUtility.magnify(termFreqsFromField(tokens, q), et.getValue());
                })
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                         Collectors.summingDouble(Map.Entry::getValue)));

        return counts;
    }

    // tiny test
    public static void main(String[] args) {
        Document doc = new Document("http://localhost");
        doc.addAnchor("http math stanford edu", 44);
        doc.addAnchor("stanford math department", 9);
        doc.end();

        Query query = new Query("2014 math requirements stanford");
        TermFreqExtractor ae = TermFreqExtractor.getExtractor(DocField.anchor);
        System.out.println(ae.extractFrom(doc, query));
    }
}
