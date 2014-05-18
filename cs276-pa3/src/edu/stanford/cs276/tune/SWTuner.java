package edu.stanford.cs276.tune;

import edu.stanford.cs276.*;
import edu.stanford.cs276.scorer.AScorer;
import edu.stanford.cs276.scorer.SmallestWindowScorer;
import edu.stanford.cs276.util.ConfigLoader;

import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kavinyao on 5/15/14.
 */
public class SWTuner {
    private static String readableConfig(Map<String, Double> config) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> et : config.entrySet()) {
            sb.append(String.format("%s=%.2f\n", et.getKey(), et.getValue()));
        }
        return sb.toString();
    }

    private static double round(double value) {
        return (double)Math.round(value * 100) / 100;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Insufficient number of arguments: <signal file> <rel file>");
            return;
        }

        TuningConfig tuningConfig = new SWTuningConfig();

        // load IDF
        IDF idfs = LoadHandler.loadIDFs();

        // init NDCG
        FileReader relFileReader = new FileReader(args[1]);
        NDCG.loadRelScores(relFileReader);

        // load train data
        Map<Query, Map<String, Document>> queryDict = LoadHandler.loadTrainData(args[0]);

        // create scorer
        AScorer scorer = new SmallestWindowScorer(idfs);

        Map<String, double[]> paramRanges = tuningConfig.getParamRanges();
        double low = paramRanges.get("B")[0];
        double high = paramRanges.get("B")[1];

        Map<String, Double> config = new HashMap<>();
        for (double b = low; b <= high; b = round(b + 0.01)) {
            // generate random config
            config.put("B", b);

            String configString = readableConfig(config);

            // load custom config
            StringReader stringReader = new StringReader(configString);
            ConfigLoader.setParameters(scorer, stringReader);

            // score documents for queries
            Map<Query, List<String>> queryRankings = Rank.score(queryDict, scorer, idfs);

            // evaluate result
            String s = generateResultString(queryRankings);
            double ndcg = NDCG.computeNDCG(new StringReader(s));

            System.out.println(String.format("%.2f %f", b, ndcg));
        }
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
