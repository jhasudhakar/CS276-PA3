package edu.stanford.cs276;

import edu.stanford.cs276.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class Rank  {
    private static Set<String> validScoreTypes = new HashSet<>(Arrays.asList(
            new String[]{"baseline", "cosine", "bm25", "extra", "window"}));

    private static Map<Query, List<String>> score(Map<Query, Map<String, Document>> queryDict,
                                                  String scoreType, IDF idfs) {
        AScorer scorer = null;

        if (scoreType.equals("baseline")) {
            scorer = new BaselineScorer();
        } else if (scoreType.equals("cosine")) {
            scorer = new CosineSimilarityScorer(idfs);
        } else if (scoreType.equals("bm25")) {
            scorer = new BM25Scorer(idfs, queryDict);
        } else if (scoreType.equals("window")) {
            //feel free to change this to match your cosine scorer if you choose to build on top of that instead
            scorer = new SmallestWindowScorer(idfs, queryDict);
        } else if (scoreType.equals("extra")) {
            scorer = new ExtraCreditScorer(idfs);
        }

        // put completed rankings here
        Map<Query, List<String>> queryRankings = new HashMap<>();

        for (Query query : queryDict.keySet()) {
            // loop through urls for query, getting scores
            List<Pair<String, Double>> urlAndScores = new ArrayList<>(queryDict.get(query).size());
            for (String url : queryDict.get(query).keySet()) {
                double score = scorer.getSimScore(queryDict.get(query).get(url), query);
                urlAndScores.add(new Pair<>(url, score));
            }

            // sort urls for query based on scores
            Collections.sort(urlAndScores, (p1, p2) -> {
                double s1 = p1.getSecond();
                double s2 = p2.getSecond();

                if (s1 < s2) {
                    return -1;
                } else if (s1 > s2) {
                    return 1;
                } else {
                    return 0;
                }
            });

            // put completed rankings into map
            List<String> rankings = urlAndScores
                    .stream()
                    .map(Pair<String, Double>::getFirst)
                    .collect(Collectors.toList());

            queryRankings.put(query, rankings);
        }

        return queryRankings;
    }

    public static void printRankedResults(Map<Query, List<String>> queryRankings) {
        for (Query query : queryRankings.keySet()) {
            System.out.println("query: " + query.getOriginalQuery());
            for (String res : queryRankings.get(query)) {
                System.out.println("  url: " + res);
            }
        }
    }

    // this probably doesn't need to be included
    // but if you output to a file, it may be easier to immediately run NDCG to score your results
    public static void writeRankedResultsToFile(Map<Query, List<String>> queryRankings, String outputFilePath) {
        try {
            File file = new File(outputFilePath);
 
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (Query query : queryRankings.keySet()) {
                StringBuilder queryBuilder = new StringBuilder();
                for (String s : query.getQueryWords()) {
                    queryBuilder.append(s);
                    queryBuilder.append(" ");
                }

                String queryStr = "query: " + queryBuilder.toString() + "\n";
                System.out.print(queryStr);
                bw.write(queryStr);

                for (String res : queryRankings.get(query)) {
                    String urlString = "  url: " + res + "\n";
                    System.out.print(urlString);
                    bw.write(urlString);
                }
            }

            bw.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        IDF idfs = LoadHandler.loadIDFs();

        if (args.length < 2) {
            System.err.println("Insufficient number of arguments: <queryDocTrainData path> taskType");
            return;
        }

        String scoreType = args[1];

        if (!validScoreTypes.contains(scoreType)) {
            System.err.println("Invalid scoring type; should be either 'baseline', 'bm25', 'cosine', 'window', or 'extra'");
            return;
        }

        Map<Query, Map<String, Document>> queryDict = null;

        /* Populate map with features from file */
        try {
            queryDict = LoadHandler.loadTrainData(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // score documents for queries
        Map<Query, List<String>> queryRankings = score(queryDict, scoreType, idfs);

        //print results and save them to file
//		String outputFilePath =  null;
//		writeRankedResultsToFile(queryRankings,outputFilePath);

        //print results
        printRankedResults(queryRankings);
    }
}
