package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;

import java.util.HashSet;

public class SmallestWindowScorer extends CosineSimilarityScorer {
    private double B = 2.0;

    public SmallestWindowScorer(IDF idfs) {
        super(idfs);
    }

    @Override
    public double getSimScore(Document d, Query q) {
        return super.getSimScore(d, q) * getBoost(d, q);
    }

    private double getBoost(Document d, Query q) {
        HashSet<String> termSet = new HashSet<>(q.getQueryWords());
        int smallestWindow = d.getSmallestWindow(termSet);
        return discount(B, smallestWindow == -1 ? Double.POSITIVE_INFINITY : smallestWindow, termSet.size());
    }

    protected double discount(double B, double smallestWindow, int Q) {
        return 1.0 + (B - 1.0) / (smallestWindow - Q + 1);
        //return 1.0 + (B - 1.0) / Math.pow(smallestWindow - Q + 1, 2);
        //return 1.0 + (B - 1.0) * Math.pow(Math.E, Q - smallestWindow);
    }
}
