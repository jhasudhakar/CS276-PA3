package edu.stanford.cs276;

import edu.stanford.cs276.scorer.AScorer;
import edu.stanford.cs276.scorer.BM25Scorer;
import edu.stanford.cs276.util.Config;

import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kavinyao on 5/15/14.
 */
public class BM25Tuner {
    private static Random R = new Random();
    private static Map<String, double[]> paramRanges;

    static {
        paramRanges = new HashMap<>();

        paramRanges.put("Bf#anchor", new double[]{0.0, 1.5});
        paramRanges.put("Bf#body", new double[]{0.0, 1.5});
        paramRanges.put("Bf#header", new double[]{0.0, 1.5});
        paramRanges.put("Bf#title", new double[]{0.0, 1.5});
        paramRanges.put("Bf#url", new double[]{0.0, 1.5});

        paramRanges.put("Wf#anchor", new double[]{1.0, 6.0});
        paramRanges.put("Wf#body", new double[]{1.0, 6.0});
        paramRanges.put("Wf#header", new double[]{1.0, 6.0});
        paramRanges.put("Wf#title", new double[]{1.0, 6.0});
        paramRanges.put("Wf#url", new double[]{1.0, 6.0});

        paramRanges.put("K1", new double[]{1.0, 50.0});
        paramRanges.put("lambda", new double[]{0.0, 1.5});
        paramRanges.put("lambdaPrime", new double[]{1.0, 4.0});
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

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Insufficient number of arguments: <signal file> <rel file>");
            return;
        }

        // save good config upon shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Wow");
            }
        });

        // load IDF
        IDF idfs = LoadHandler.loadIDFs();

        // init NDCG
        FileReader relFileReader = new FileReader(args[1]);
        NDCG.loadRelScores(relFileReader);

        // load train data
        Map<Query, Map<String, Document>> queryDict = LoadHandler.loadTrainData(args[0]);

        // create scorer
        AScorer scorer = new BM25Scorer(idfs, queryDict);

        // generate random config
        Map<String, Double> config = generateRandomConfig();
        StringReader stringReader = new StringReader(readableConfig(config));
        // load custom config
        Config.setParameters(scorer, stringReader);

        // score documents for queries
        Map<Query, List<String>> queryRankings = Rank.score(queryDict, scorer, idfs);

        // evalute result
        String s = generateResultString(queryRankings);
        System.out.println(NDCG.computeNDCG(new StringReader(s)));
    }

    public static String generateResultString(Map<Query, List<String>> queryRankings) {
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
