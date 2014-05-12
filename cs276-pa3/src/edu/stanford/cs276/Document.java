package edu.stanford.cs276;

import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.doc.FieldProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Document {
    // fields
    // all fields are trimmed and in lowercase
    private String url = null;
    private String title = null;
    private List<String> headers = new ArrayList<>();
    // term -> [list of positions]
    private Map<String, List<Integer>> bodyHits = new HashMap<>();
    // anchor text -> reference count
    private Map<String, Integer> anchors = new HashMap<>();

    // other attributes
    private int bodyLength = 0;
    private int pageRank = 0;

    // cached tokens: field -> [tokens]
    private Map<DocField, List<String>> fieldTokens;

    public Document(String url) {
        this.url = normalize(url);
        this.fieldTokens = new HashMap<>();
    }

    private static String normalize(String s) {
        return s.trim().toLowerCase();
    }

    public Map<String, List<Integer>> getBodyHits() {
        return bodyHits;
    }

    public void setTitle(String title) {
        this.title = normalize(title);
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void addHeader(String header) {
        this.headers.add(normalize(header));
    }

    public void addBodyHits(String term, List<Integer> positions) {
        term = normalize(term);

        if (this.bodyHits.containsKey(term)) {
            throw new IllegalStateException("Duplicate term in body");
        }

        this.bodyHits.put(term, positions);
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public int getPageRank() {
        return pageRank;
    }

    public void setPageRank(int pageRank) {
        this.pageRank = pageRank;
    }

    public void addAnchor(String anchorText, int count) {
        anchorText = normalize(anchorText);

        if (this.anchors.containsKey(anchorText)) {
            throw new IllegalStateException("Duplicate anchor text");
        }

        this.anchors.put(anchorText, count);
    }

    /**
     * Call this method after this document is constructed.
     */
    public void end() {
        fieldTokens.put(DocField.url, FieldProcessor.splitURL(this.url));
        fieldTokens.put(DocField.title, FieldProcessor.splitTitle(this.title));
        fieldTokens.put(DocField.header, FieldProcessor.splitHeaders(this.headers));
        fieldTokens.put(DocField.anchor, FieldProcessor.splitAnchors(this.anchors));
    }

    public List<String> getFieldTokens(DocField f) {
        if (f == DocField.body) {
            throw new IllegalStateException("Cannot get tokens of body.");
        }

        return fieldTokens.get(f);
    }

    public int getNumFieldTokens(DocField f) {
        if (f == DocField.body) {
            return this.bodyLength;
        }

        return fieldTokens.get(f).size();
    }

    // For debug
    public String toString() {
        StringBuilder result = new StringBuilder();

        String NEW_LINE = System.getProperty("line.separator");
        if (title != null) result.append("title: " + title + NEW_LINE);
        if (headers.size() > 0) result.append("headers: " + headers.toString() + NEW_LINE);
        if (bodyHits.size() > 0) result.append("body_hits: " + bodyHits.toString() + NEW_LINE);
        if (bodyLength != 0) result.append("body_length: " + bodyLength + NEW_LINE);
        if (pageRank != 0) result.append("page_rank: " + pageRank + NEW_LINE);
        if (anchors.size() > 0) result.append("anchors: " + anchors.toString() + NEW_LINE);

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || !(obj instanceof Document)) {
            return false;
        }

        Document another = (Document) obj;
        return this.url.equals(another.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
