package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;

import java.util.List;

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
        List<String> queryWords = q.getQueryWords();
        int smallestWindow = d.getSmallestWindow(queryWords);
        if (smallestWindow == -1) {
            return 1;
        } else {
            return discount(smallestWindow, queryWords.size());
        }
    }

    private double discount(int smallestWindow, int Q) {
        return 1.0 + (B - 1.0) / (smallestWindow - Q + 1);
    }
}
