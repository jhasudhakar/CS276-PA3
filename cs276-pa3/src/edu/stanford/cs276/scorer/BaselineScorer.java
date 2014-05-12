package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;

public class BaselineScorer extends AScorer
{

    public BaselineScorer() {
        //don't need idfs for the baseline
        super(null);
    }

    // sum over the length of the bodyHits array for all query terms
    @Override
    public double getSimScore(Document d, Query q) {
        return d.getNumFieldTokens(DocField.body);
    }
}
