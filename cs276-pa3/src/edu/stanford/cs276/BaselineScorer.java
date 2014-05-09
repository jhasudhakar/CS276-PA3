package edu.stanford.cs276;

public class BaselineScorer extends AScorer
{
	
	public BaselineScorer()
	{
		//don't need idfs for the baseline
		super(null);
	}
	
	//We sum over the length of the bodyHits array for all query terms
	@Override
	public double getSimScore(Document d, Query q) 
	{
		double score = 0.0;
		if (d.bodyHits !=null)
		{
			for (String term : d.bodyHits.keySet())
				score += d.bodyHits.get(term).size();
		}
		
		return score;
	}

}
