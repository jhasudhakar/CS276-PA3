package edu.stanford.cs276.tune;

import edu.stanford.cs276.*;
import edu.stanford.cs276.scorer.AScorer;
import edu.stanford.cs276.scorer.BM25Scorer;
import edu.stanford.cs276.util.ConfigLoader;
import edu.stanford.cs276.util.Pair;
import edu.stanford.cs276.util.SerializationHelper;

import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

/**
 * Created by weiwei on 5/17/14.
 */
public class HillClimbingTuner {
    private static int counter = 0;
    private static Random R = new Random();
    private static Map<String, double[]> paramRanges;

    private static String[] parameters = {"Bf#anchor", "Bf#body", "Bf#header", "Bf#title", "Bf#url", "Wf#anchor", "Wf#body", "Wf#header", "Wf#title", "Wf#url", "K1", "lambda", "lambdaPrime"};

    static {
        paramRanges = new HashMap<>();
        paramRanges.put("Bf#anchor", new double[]{0.0, 1.0});
        paramRanges.put("Bf#body", new double[]{0.0, 1.0});
        paramRanges.put("Bf#header", new double[]{0.0, 1.0});
        paramRanges.put("Bf#title", new double[]{0.0, 1.0});
        paramRanges.put("Bf#url", new double[]{0.0, 1.0});

        paramRanges.put("Wf#anchor", new double[]{0.0, 4.0});
        paramRanges.put("Wf#body", new double[]{0.0, 2.0});
        paramRanges.put("Wf#header", new double[]{1.0, 4.0});
        paramRanges.put("Wf#title", new double[]{1.0, 4.0});
        paramRanges.put("Wf#url", new double[]{1.0, 4.0});

        paramRanges.put("K1", new double[]{1.0, 3.0});
        paramRanges.put("lambda", new double[]{0, 5.0});
        paramRanges.put("lambdaPrime", new double[]{1.0, 6.0});
    }

    private static Map<String, Double> generateRandomConfig() {
        Map<String, Double> config = new HashMap<>();

        for (Map.Entry<String, double[]> et : paramRanges.entrySet()) {
            double[] range = et.getValue();
            double val = range[0] + (range[1] - range[0]) * R.nextDouble();
            config.put(et.getKey(), val);
        }
        return config;
    }

    private static String readableConfig(Map<String, Double> config) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> et : config.entrySet()) {
            sb.append(String.format("%s=%f\n", et.getKey(), et.getValue()));
        }
        return sb.toString();
    }

    private static List<Map<String, Double>> getNeighbors(Map<String, Double> config) {
        List<Map<String, Double>> neighbors = new ArrayList<>();
        if (R.nextDouble() < 0.05) {
            // Random restarts
            System.out.println("Random Restart!");
            Map<String, Double> newConfig = generateRandomConfig();
            neighbors.add(newConfig);
            return neighbors;
        }
        String key = parameters[getCounter()];
        double lower = paramRanges.get(key)[0];
        double upper = paramRanges.get(key)[1];
        for (double d = lower; d <= upper; d += 0.2) {
            Map<String, Double> newConfig = new HashMap<>(config);
            newConfig.put(key, d);
            neighbors.add(newConfig);
        }
        return neighbors;
    }

    private static int getCounter() {
        counter++;
        if (counter == 13) {
            counter = 0;
        }
        return counter;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Insufficient number of arguments: <signal file> <rel file>");
            return;
        }
        // Load IDF.
        IDF idfs = LoadHandler.loadIDFs();
        // Init NDCG.
        FileReader relFileReader = new FileReader(args[1]);
        NDCG.loadRelScores(relFileReader);
        // Load train data.
        Map<Query, Map<String, Document>> queryDict = LoadHandler.loadTrainData(args[0]);
        // Create scorer.
        AScorer scorer = new BM25Scorer(idfs, queryDict);
        // Good config.
        List<Pair<Map<String, Double>, Double>> goodConfigs = new ArrayList<>();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                long unixTime = System.currentTimeMillis() / 1000L;
                String fileName = String.format("bm25-good-config-%d.ser", unixTime);
                SerializationHelper.saveObjectToFile(goodConfigs, fileName);
            }
        });
        // Inital Configuration.
        Map<String, Double> config = generateRandomConfig();
        double bestSoFar = 0;
        Set<Map<String, Double>> uniqueConfig = new HashSet<>();
        while (true) {
            List<Map<String, Double>> configList = getNeighbors(config);
            bestSoFar = 0;
            for (Map<String, Double> c : configList) {
                if (uniqueConfig.contains(c)) {
                    continue;
                } else {
                    uniqueConfig.add(c);
                }
                StringReader stringReader;
                stringReader = new StringReader(readableConfig(c));
                ConfigLoader.setParameters(scorer, BM25Scorer.class, stringReader);
                // Score documents.
                Map<Query, List<String>> queryRankings = Rank.score(queryDict, scorer, idfs);
                // Evaluate results.
                String s = generateResultString(queryRankings);
                double ndcg = NDCG.computeNDCG(new StringReader(s));
                if (ndcg > bestSoFar) {
                    bestSoFar = ndcg;
                    config = c;
                }
                if (ndcg > 0.88) {
                    System.out.println("!!!!!" + ndcg);
                    if (ndcg > 0.885) {
                        System.out.println("===== ===== ===== =====");
                        for (Map.Entry<String, Double> et : c.entrySet()) {
                            System.out.println(et.getKey() + ": " + et.getValue());
                        }
                        System.out.println("===== ===== ===== =====");
                        System.out.println("");
                        goodConfigs.add(new Pair<>(c, ndcg));
                    }
                }
            }
            System.out.println(bestSoFar);
        }
    }


    private static String generateResultString(Map<Query, List<String>> queryRankings) {
        StringBuilder sb = new StringBuilder();
        for (Query query : queryRankings.keySet()) {
            sb.append("query: " + query.getOriginalQuery() + "\n");
            for (String res : queryRankings.get(query)) {
                sb.append("  url: " + res + "\n");
            }
        }

        return sb.toString();
    }
}
