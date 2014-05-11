package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.stanford.cs276.doc.FieldProcessor.*;

public class BM25Scorer extends AScorer {
    // static variables (parameters)
    private static Map<DocField, Double> Bf;
    private static Map<DocField, Double> Wf;
    private double K1 = -1;
    private double lambda = -1;
    private double lambdaPrime = -1;


    // initialize weights
    static {
        Bf = new HashMap<>();
        Bf.put(DocField.url, 1.0);
        Bf.put(DocField.title, 1.0);
        Bf.put(DocField.header, 1.0);
        Bf.put(DocField.body, 1.0);
        Bf.put(DocField.anchor, 1.0);

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

    private static Map<Document, Double> lengthsOfField(Collection<Map<String, Document>> docs,
                                                 Function<Document, Integer> getLength) {
        return docs
                .stream()
                .map(m -> m.values())
                .flatMap(c -> c.stream())
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
        Collection<Map<String, Document>> docs = queryDict.values();

        // compute length of each field
        lengths = new HashMap<>();
        lengths.put(DocField.url, lengthsOfField(docs, d -> splitURL(d).size()));
        lengths.put(DocField.title, lengthsOfField(docs, d -> splitTitle(d).size()));
        lengths.put(DocField.header, lengthsOfField(docs, d -> splitHeaders(d).size()));
        lengths.put(DocField.body, lengthsOfField(docs, d -> d.bodyLength));
        lengths.put(DocField.anchor, lengthsOfField(docs, d -> splitAnchors(d).size()));

        // compute average lengths of each field
        avgLengths = new HashMap<>();
        for (DocField docField : DocField.values()) {
            avgLengths.put(docField, averageFieldLength(lengths.get(docField)));
        }

        pagerankScores = docs
                .stream()
                .map(m -> m.values())
                .flatMap(c -> c.stream())
                .collect(Collectors.toMap(d -> d, d -> new Double(d.pageRank)));

        //normalize avgLengths
        //for (String tfType : this.TF_TYPES)
        //{
            /*
             * @//TODO : Your code here
             */
        //}

    }

    ////////////////////////////////////


    public double getNetScore(Map<DocField, Map<String, Double>> tfs, Query q, Map<String, Double> tfQuery, Document d) {
        double score = 0.0;

        /*
         * @//TODO : Your code here
         */

        return score;
    }

    //do bm25 normalization
    public void normalizeTFs(Map<DocField, Map<String, Double>> tfs, Document d, Query q) {
        /*
         * @//TODO : Your code here
         */
    }


    @Override
    public double getSimScore(Document d, Query q) {

        Map<DocField, Map<String, Double>> tfs = this.getDocTermFreqs(d, q);

        this.normalizeTFs(tfs, d, q);

        Map<String, Double> tfQuery = getQueryFreqs(q);


        return getNetScore(tfs, q, tfQuery, d);
    }


}
