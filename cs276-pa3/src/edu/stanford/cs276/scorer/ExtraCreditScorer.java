package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;

public class ExtraCreditScorer extends AScorer
{

    public ExtraCreditScorer(IDF idfs) {
        super(idfs);
    }

    @Override
    public double getSimScore(Document d, Query q) {

        return 0;
    }

    /*
     * @//TODO : Your code here
     */
}
