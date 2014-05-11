package edu.stanford.cs276.doc;

import edu.stanford.cs276.Document;

import java.util.*;

/**
 * Created by kavinyao on 5/10/14.
 */
public class FieldProcessor {
    protected static final String NON_WORD_REGEX = "\\W+";
    protected static final String WHITE_SPACE_REGEX = "\\s+";
    protected static List<String> EMPTY_LIST = new ArrayList<>();

    public static List<String> splitURL(Document d) {
        return tokenize(d.url, NON_WORD_REGEX);
    }

    public static List<String> splitTitle(Document d) {
        if (d.title == null) {
            return EMPTY_LIST;
        }

        return tokenize(d.title.toLowerCase(), WHITE_SPACE_REGEX);
    }

    public static List<String> splitHeaders(Document d) {
        if (d.headers == null) {
            return EMPTY_LIST;
        }

        List<String> headerWords = new ArrayList<>();

        for (String header : d.headers) {
            headerWords.addAll(tokenize(header.toLowerCase(), WHITE_SPACE_REGEX));
        }

        return headerWords;
    }

    public static List<String> splitAnchors(Document d) {
        if (d.anchors == null) {
            return EMPTY_LIST;
        }

        List<String> anchorWords = new ArrayList<>();

        for (String anchorText : d.anchors.keySet()) {
            anchorWords.addAll(tokenize(anchorText.toLowerCase(), WHITE_SPACE_REGEX));
        }

        return anchorWords;
    }

    protected static List<String> tokenize(String s, String sep) {
        return Arrays.asList(s.split(sep));
    }
}
