package edu.stanford.cs276;

import edu.stanford.cs276.util.MapUtility;

import java.io.*;
import java.util.*;

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
        Query query = null;

        /* feature dictionary: Query -> (url -> Document)  */
        Map<Query, Map<String, Document>> queryDict = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(":", 2);
            String key = tokens[0].trim();
            String value = tokens[1].trim();

            if (key.equals("query")) {
                query = new Query(value.trim().toLowerCase());
                queryDict.put(query, new HashMap<>());
            } else if (key.equals("url")) {
                url = value.trim().toLowerCase();
                queryDict.get(query).put(url, new Document(url));
            } else if (key.equals("title")) {
                queryDict.get(query).get(url).title = value.trim();
            } else if (key.equals("header")) {
                if (queryDict.get(query).get(url).headers == null) {
                    queryDict.get(query).get(url).headers = new ArrayList<>();
                }
                queryDict.get(query).get(url).headers.add(value.trim());
            } else if (key.equals("body_hits")) {
                if (queryDict.get(query).get(url).bodyHits == null) {
                    queryDict.get(query).get(url).bodyHits = new HashMap<>();
                }

                String[] temp = value.split(" ", 2);
                String term = temp[0].trim();
                List<Integer> positions_int;

                if (!queryDict.get(query).get(url).bodyHits.containsKey(term)) {
                    positions_int = new ArrayList<>();
                    queryDict.get(query).get(url).bodyHits.put(term, positions_int);
                } else {
                    positions_int = queryDict.get(query).get(url).bodyHits.get(term);
                }

                String[] positions = temp[1].trim().split(" ");
                for (String position : positions) {
                    positions_int.add(Integer.parseInt(position));
                }
            } else if (key.equals("body_length")) {
                queryDict.get(query).get(url).bodyLength = Integer.parseInt(value);
            } else if (key.equals("pagerank ")) {
                    queryDict.get(query).get(url).pageRank = Integer.parseInt(value);
            } else if (key.equals("anchor_text")) {
                anchor_text = value.trim();
                if (queryDict.get(query).get(url).anchors == null) {
                    queryDict.get(query).get(url).anchors = new HashMap<>();
                }
            } else if (key.equals("stanford_anchor_count")) {
                queryDict.get(query).get(url).anchors.put(anchor_text, Integer.parseInt(value));
            }
        }

        reader.close();

        return queryDict;
    }

    /**
     * Load IDF data from file.
     * @return
     */
    public static IDF loadIDFs()
    {
        IDF IDF;

        try {
            FileInputStream fis = new FileInputStream(Config.IDF_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            IDF = (IDF) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
            return null;
        }

        return IDF;
    }

    public static void saveIDFs(IDF IDF) {
        try {
            FileOutputStream fos = new FileOutputStream(Config.IDF_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(IDF);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                // store terms in this document
                Set<String> terms = new HashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    terms.addAll(Arrays.asList(line.trim().split("\\s+")));
                }

                reader.close();

                // increment document count for each term
                for (String term : terms) {
                    MapUtility.incrementCount(term, termDocCount);
                }
            }

            System.out.println("Finished processing " + blockDir.getName());
        }

        System.out.println(totalDocCount);

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
