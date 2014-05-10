package edu.stanford.cs276;

import java.util.List;
import java.util.Map;

public class Document {
    // all fields are trimmed
    // url field is lowercase
    public String url = null;
    public String title = null;
    public List<String> headers = null;
    public Map<String, List<Integer>> bodyHits = null; // term -> [list of positions]
    public int bodyLength = 0;
    public int pageRank = 0;
    public Map<String, Integer> anchors = null; // term -> anchor_count

    public Document(String url)
    {
        this.url=url;
    }

    // For debug
    public String toString() {
        StringBuilder result = new StringBuilder();

        String NEW_LINE = System.getProperty("line.separator");
        if (title != null) result.append("title: " + title + NEW_LINE);
        if (headers != null) result.append("headers: " + headers.toString() + NEW_LINE);
        if (bodyHits != null) result.append("body_hits: " + bodyHits.toString() + NEW_LINE);
        if (bodyLength != 0) result.append("body_length: " + bodyLength + NEW_LINE);
        if (pageRank != 0) result.append("page_rank: " + pageRank + NEW_LINE);
        if (anchors != null) result.append("anchors: " + anchors.toString() + NEW_LINE);

        return result.toString();
    }
}
