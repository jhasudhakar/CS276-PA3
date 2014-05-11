package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.util.MapUtility;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.stanford.cs276.doc.FieldProcessor.*;

public class BM25Scorer extends AScorer {
    // static variables (parameters)
    private static Map<DocField, Double> Bf;
    private static Map<DocField, Double> Wf;
    private double K1 = 1.5;
    private double lambda = 0;
    private double lambdaPrime = 0.5;


    // initialize weights
    static {
        Bf = new HashMap<>();
        Bf.put(DocField.url, 0.75);
        Bf.put(DocField.title, 0.75);
        Bf.put(DocField.header, 0.75);
        Bf.put(DocField.body, 0.75);
        Bf.put(DocField.anchor, 0.75);

        Wf = new HashMap<>();
        Wf.put(DocField.url, 1.0);
        Wf.put(DocField.title, 1.0);
        Wf.put(DocField.header, 1.0);
        Wf.put(DocField.body, 1.0);
        Wf.put(DocField.anchor, 1.0);
    }

    // instance variables
    // query -> url -> document
    private Map<Query, Map<String, Document>> queryDict;
    // field -> document -> length
    Map<DocField, Map<Document, Double>> lengths;
    // field -> avg. length
    Map<DocField, Double> avgLengths;
    // document -> pagerank
    Map<Document, Double> pagerankScores;

    public BM25Scorer(IDF idfs, Map<Query, Map<String, Document>> queryDict) {
        super(idfs);

        this.queryDict = queryDict;

        calcAverageLengths();
    }

    /**
     * Compute length of given field for every document.
     * @param docs must not contain duplicate
     * @param getLength length getter for field
     * @return
     */
    private static Map<Document, Double> lengthsOfField(List<Document> docs,
                                                 Function<Document, Integer> getLength) {
        return docs
                .stream()
                .collect(Collectors.toMap(d -> d, d -> getLength.apply(d).doubleValue()));
    }

    private static Double averageFieldLength(Map<Document, Double> fieldLengths) {
        return fieldLengths.values()
                .stream()
                .mapToDouble(d -> d)
                .average()
                .getAsDouble();
    }

    // sets up average lengths for bm25, also handles pagerank
    public void calcAverageLengths() {
        List<Document> uniqueDocs = queryDict.values()
                .stream()
                .flatMap(m -> m.values().stream())
                .distinct()
                .collect(Collectors.toList());

        // compute length of each field
        lengths = new HashMap<>();
        lengths.put(DocField.url, lengthsOfField(uniqueDocs, d -> splitURL(d).size()));
        lengths.put(DocField.title, lengthsOfField(uniqueDocs, d -> splitTitle(d).size()));
        lengths.put(DocField.header, lengthsOfField(uniqueDocs, d -> splitHeaders(d).size()));
        lengths.put(DocField.body, lengthsOfField(uniqueDocs, d -> d.bodyLength));
        lengths.put(DocField.anchor, lengthsOfField(uniqueDocs, d -> splitAnchors(d).size()));

        // compute average lengths of each field
        avgLengths = new HashMap<>();
        for (DocField docField : DocField.values()) {
            avgLengths.put(docField, averageFieldLength(lengths.get(docField)));
        }

        pagerankScores = uniqueDocs
                .stream()
                .collect(Collectors.toMap(d -> d, d -> new Double(d.pageRank)));
    }

    private double getTermWeight(Document d, Map<DocField, Map<String, Double>> tfs, String t) {
        return Arrays.asList(DocField.values())
                .stream()
                .map(f -> {
                    double tf = MapUtility.getWithFallback(tfs.get(f), t, 0.0);
                    double ftf = tf / (1 + Bf.get(f) * lengths.get(f).get(d) / avgLengths.get(f) - 1);
                    return Wf.get(f) * ftf;
                })
                .mapToDouble(x -> x)
                .sum();
    }

    private double V(int pageRank) {
        return Math.log(pageRank + lambdaPrime);
    }

    @Override
    public double getSimScore(Document d, Query q) {
        Map<DocField, Map<String, Double>> tfs = getDocTermFreqs(d, q);
        Map<String, Double> tfQuery = getQueryFreqs(q);

        return tfQuery.keySet()
                .stream()
                .map(t -> {
                    double idf = idfs.getValue(t);
                    double wdt = getTermWeight(d, tfs, t);
                    return idf * wdt * (1 + K1) / (wdt + K1) + lambda * V(d.pageRank);
                })
                .mapToDouble(x -> x)
                .sum();
    }
}
