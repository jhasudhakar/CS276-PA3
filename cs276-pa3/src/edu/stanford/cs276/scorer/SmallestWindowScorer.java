package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;

public class SmallestWindowScorer extends CosineSimilarityScorer {
    double B = -1;

    public SmallestWindowScorer(IDF idfs) {
        super(idfs);
    }

    @Override
    public double getSimScore(Document d, Query q) {
        return super.getSimScore(d, q) * getBoost(d, q);
    }

    private double getBoost(Document d, Query q) {
        return 1;
    }
}
