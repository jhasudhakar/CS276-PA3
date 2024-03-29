package edu.stanford.cs276;

import edu.stanford.cs276.util.MapUtility;
import edu.stanford.cs276.util.SerializationHelper;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LoadHandler 
{
    public static Map<Query, Map<String, Document>> loadTrainData(String feature_file_name) throws Exception {
        File feature_file = new File(feature_file_name);
        if (!feature_file.exists()) {
            System.err.println("Invalid feature file name: " + feature_file_name);
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader(feature_file));
        String line = null, url= null, anchor_text = null;
        Query currentQuery = null;
        Map<String, Document> currentDocuments = null;
        Document currentDocument = null;

        /* feature dictionary: Query -> (url -> Document)  */
        Map<Query, Map<String, Document>> queryDict = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(":", 2);
            String key = tokens[0].trim();
            String value = tokens[1].trim();

            if (key.equals("query")) {
                currentQuery = new Query(value.trim());
                currentDocuments = new HashMap<>();
                queryDict.put(currentQuery, currentDocuments);
            } else if (key.equals("url")) {
                url = value.trim();
                currentDocument = new Document(url);
                currentDocuments.put(url, currentDocument);
            } else if (key.equals("title")) {
                currentDocument.setTitle(value);
            } else if (key.equals("header")) {
                currentDocument.addHeader(value);
            } else if (key.equals("body_hits")) {
                String[] temp = value.split(" ", 2);
                String term = temp[0].trim();
                List<Integer> positions = Arrays.asList(temp[1].trim().split(" "))
                        .stream()
                        .map(pos -> Integer.parseInt(pos))
                        .collect(Collectors.toList());

                currentDocument.addBodyHits(term, positions);
            } else if (key.equals("body_length")) {
                currentDocument.setBodyLength(Integer.parseInt(value));
            } else if (key.equals("pagerank")) {
                currentDocument.setPageRank(Integer.parseInt(value));
            } else if (key.equals("anchor_text")) {
                anchor_text = value.trim();
            } else if (key.equals("stanford_anchor_count")) {
                currentDocument.addAnchor(anchor_text, Integer.parseInt(value));
            }
        }

        // finish build documents
        queryDict.values()
                .stream()
                .flatMap(ds -> ds.values().stream())
                .forEach(d -> d.end());

        reader.close();

        return queryDict;
    }

    /**
     * Load IDF data from file.
     * @return
     */
    public static IDF loadIDFs() {
        return (IDF) SerializationHelper.loadObjectFromFile(Config.IDF_FILE);
    }

    public static void saveIDFs(IDF IDF) {
        SerializationHelper.saveObjectToFile(IDF, Config.IDF_FILE);
    }

    /**
     * Build IDF from training documents. The documents should be organized in a block fashion.
     * @param dataDir the directory of all blocks
     * @return
     * @throws IOException
     */
    public static IDF buildIDFs(String dataDir) throws IOException {

        /* Get root directory */
        String root = dataDir;
        File rootdir = new File(root);
        if (!rootdir.exists() || !rootdir.isDirectory()) {
            System.err.println("Invalid data directory: " + root);
            return null;
        }

        File[] dirlist = rootdir.listFiles();

        int totalDocCount = 0;

        // counts number of documents in which each term appears
        Map<String, Integer> termDocCount = new HashMap<>();

        for (File blockDir : dirlist) {
            File[] docFiles = blockDir.listFiles();
            for (File docFile : docFiles) {
                // increment total document count
                totalDocCount++;

                BufferedReader reader = new BufferedReader(new FileReader(docFile));
                // store unique terms in this document
                Set<String> terms = new HashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    terms.addAll(Arrays.asList(line.trim().toLowerCase().split("\\s+")));
                }

                reader.close();

                // increment document count for each term
                for (String term : terms) {
                    MapUtility.incrementCount(term, termDocCount);
                }
            }

            System.out.println("Finished processing " + blockDir.getName());
        }

        System.out.println("#documents = " + totalDocCount);

        return new IDF(termDocCount, totalDocCount);
    }

    public static void main(String[] args) throws IOException {
        IDF IDF = buildIDFs(args[0]);

        // some tests
        System.out.println("IDF(the) = " + IDF.getValue("the"));
        System.out.println("IDF(stanford) = " + IDF.getValue("stanford"));
        System.out.println("IDF(chris) = " + IDF.getValue("chris"));
        System.out.println("IDF(xxxyyyy) = " + IDF.getValue("xxxyyyy"));

        saveIDFs(IDF);
    }
}
