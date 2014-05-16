package edu.stanford.cs276;

import edu.stanford.cs276.scorer.AScorer;
import edu.stanford.cs276.scorer.BM25Scorer;
import edu.stanford.cs276.util.Config;
import edu.stanford.cs276.util.Pair;
import edu.stanford.cs276.util.SerializationHelper;

import java.io.FileReader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kavinyao on 5/15/14.
 */
public class BM25Tuner {
    private static Random R = new Random();
    private static Map<String, double[]> paramRanges;

    static {
        paramRanges = new HashMap<>();

        paramRanges.put("Bf#anchor", new double[]{0.0, 1.1});
        paramRanges.put("Bf#body", new double[]{0.2, 1.1});
        paramRanges.put("Bf#header", new double[]{0.2, 1.1});
        paramRanges.put("Bf#title", new double[]{0.2, 1.2});
        paramRanges.put("Bf#url", new double[]{0.3, 1.6});

        paramRanges.put("Wf#anchor", new double[]{1.0, 6.0});
        paramRanges.put("Wf#body", new double[]{1.0, 6.0});
        paramRanges.put("Wf#header", new double[]{1.0, 6.0});
        paramRanges.put("Wf#title", new double[]{1.0, 7.0});
        paramRanges.put("Wf#url", new double[]{1.0, 6.0});

        paramRanges.put("K1", new double[]{30.0, 50.0});
        paramRanges.put("lambda", new double[]{1.0, 5.0});
        paramRanges.put("lambdaPrime", new double[]{1.0, 6.0});
    }

    private static double round(double value) {
        return (double)Math.round(value * 100) / 100;
    }

    private static Map<String, Double> generateRandomConfig() {
        Map<String, Double> config = new HashMap<>();

        for (Map.Entry<String, double[]> et : paramRanges.entrySet()) {
            double[] range = et.getValue();
            double val = round(range[0] + (range[1] - range[0]) * R.nextDouble());
            config.put(et.getKey(), val);
        }

        return config;
    }

    private static String readableConfig(Map<String, Double> config) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> et : config.entrySet()) {
            sb.append(String.format("%s=%.2f\n", et.getKey(), et.getValue()));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Insufficient number of arguments: <signal file> <rel file>");
            return;
        }

        List<Pair<Map<String, Double>, Double>> goodConfigs = new ArrayList<>();

        // save good config upon shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                long unixTime = System.currentTimeMillis() / 1000L;
                String fileName = String.format("bm25-good-config-%d.ser", unixTime);
                SerializationHelper.saveObjectToFile(goodConfigs, fileName);
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

        Set<String> uniqeConfigs = new HashSet<>();

        // run forever
        while (true) {
            // generate random config
            Map<String, Double> config = generateRandomConfig();
            String configString = readableConfig(config);

            // avoid duplicate
            if (uniqeConfigs.contains(configString)) {
                continue;
            }
            uniqeConfigs.add(configString);

            // load custom config
            StringReader stringReader = new StringReader(configString);
            Config.setParameters(scorer, stringReader);

            // score documents for queries
            Map<Query, List<String>> queryRankings = Rank.score(queryDict, scorer, idfs);

            // evalute result
            String s = generateResultString(queryRankings);
            double ndcg = NDCG.computeNDCG(new StringReader(s));

            // save good config
            if (ndcg > 0.885) {
                if (ndcg > 0.888) {
                    System.out.println("!!!!!! " + ndcg);
                } else {
                    System.out.println(ndcg);
                }
                goodConfigs.add(new Pair<>(config, ndcg));
            }
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

    public static class ResultViewer {
        public static void main(String[] args) {
            if (args.length < 1) {
                System.err.println("Not enough argument!");
                return;
            }

            List<Pair<Map<String, Double>, Double>> goodConfigs =
                    (List<Pair<Map<String, Double>, Double>>)
                            SerializationHelper.loadObjectFromFile(args[0]);

            List<String> orderedKeys = null;
            if (goodConfigs.size() > 0) {
                orderedKeys = goodConfigs
                        .get(0)
                        .getFirst()
                        .keySet()
                        .stream()
                        .sorted()
                        .collect(Collectors.toList());

                System.out.print("NDCG\t");
                for (String key : orderedKeys) {
                    if (key.length() < 6) {
                        System.out.print(key + "\t");
                    } else {
                        System.out.print(key.substring(0, 6) + "\t");
                    }
                }
                System.out.println();

                int fields = orderedKeys.size() + 1;
                // http://stackoverflow.com/a/4903603
                String bars = new String(new char[fields]).replace("\0", "--------");
                System.out.println(bars);
            }

            Collections.sort(goodConfigs, (p1, p2) -> {
                return -p1.getSecond().compareTo(p2.getSecond());
            });

            for (Pair<Map<String, Double>, Double> config : goodConfigs) {
                System.out.print(String.format("%.4f\t", config.getSecond()));

                Map<String, Double> params = config.getFirst();
                for (String key : orderedKeys) {
                    System.out.print(String.format("%.4f\t", params.get(key)));
                }
                System.out.println();
            }
        }
    }
}
