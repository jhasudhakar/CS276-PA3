package edu.stanford.cs276;

public class ExtraCreditScorer extends AScorer
{

    public ExtraCreditScorer(IDF idfs)
    {
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
