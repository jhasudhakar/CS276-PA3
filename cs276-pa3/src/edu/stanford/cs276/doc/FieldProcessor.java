package edu.stanford.cs276.doc;

import java.util.*;

/**
 * Created by kavinyao on 5/10/14.
 */
public class FieldProcessor {
    protected static final String NON_WORD_REGEX = "\\W+";
    protected static final String WHITE_SPACE_REGEX = "\\s+";
    public static List<String> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>());

    /**
     * URL most not be null.
     * @param url
     * @return
     */
    public static List<String> splitURL(String url) {
        return tokenize(url, NON_WORD_REGEX);
    }

    /**
     * title must be in lowercased if not null.
     * @param title
     * @return
     */
    public static List<String> splitTitle(String title) {
        if (title == null) {
            return EMPTY_LIST;
        }

        return tokenize(title, WHITE_SPACE_REGEX);
    }

    public static List<String> splitHeaders(List<String> headers) {
        if (headers.size() == 0) {
            return EMPTY_LIST;
        }

        List<String> headerWords = new ArrayList<>();

        for (String header : headers) {
            headerWords.addAll(tokenize(header.toLowerCase(), WHITE_SPACE_REGEX));
        }

        return headerWords;
    }

    public static List<String> splitAnchors(Map<String, Integer> anchors) {
        if (anchors.size() == 0) {
            return EMPTY_LIST;
        }

        List<String> anchorWords = new ArrayList<>();

        for (String anchorText : anchors.keySet()) {
            anchorWords.addAll(tokenize(anchorText.toLowerCase(), WHITE_SPACE_REGEX));
        }

        return anchorWords;
    }

    protected static List<String> tokenize(String s, String sep) {
        return Arrays.asList(s.split(sep));
    }
}
