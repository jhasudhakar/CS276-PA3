package edu.stanford.cs276;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Query {
    private String originalQuery;
    private List<String> queryWords;
	
	public Query(String query) {
        originalQuery = query;
		queryWords = new ArrayList<>(Arrays.asList(query.split(" ")));
	}

    public List<String> getQueryWords() {
        return queryWords;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    @Override
    public int hashCode() {
        return originalQuery.hashCode();
    }
}
