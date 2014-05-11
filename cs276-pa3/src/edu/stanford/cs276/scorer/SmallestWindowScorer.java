package edu.stanford.cs276.scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.IDF;
import edu.stanford.cs276.Query;
import edu.stanford.cs276.doc.DocField;

import java.util.Map;

//doesn't necessarily have to use task 2 (could use task 1, in which case, you'd probably like to extend CosineSimilarityScorer instead)
public class SmallestWindowScorer extends BM25Scorer
{

    /////smallest window specifichyperparameters////////
    double B = -1;    	    
    double boostmod = -1;
    
    //////////////////////////////

    public SmallestWindowScorer(IDF idfs,Map<Query,Map<String, Document>> queryDict)
    {
        super(idfs, queryDict);
        handleSmallestWindow();
    }


    public void handleSmallestWindow()
    {
        /*
         * @//TODO : Your code here
         */
    }


    public double checkWindow(Query q,String docstr,double curSmallestWindow,boolean isBodyField)
    {
        /*
         * @//TODO : Your code here
         */
        return -1;
    }


    @Override
    public double getSimScore(Document d, Query q) {
        Map<DocField,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);

        Map<String,Double> tfQuery = getQueryFreqs(q);

        return 0;
    }

}
