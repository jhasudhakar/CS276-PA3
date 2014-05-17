package edu.stanford.cs276.tune;

import edu.stanford.cs276.*;
import edu.stanford.cs276.scorer.AScorer;
import edu.stanford.cs276.scorer.BM25Scorer;
import edu.stanford.cs276.scorer.CosineSimilarityScorer;
import edu.stanford.cs276.util.ConfigLoader;
import edu.stanford.cs276.util.Pair;
import edu.stanford.cs276.util.SerializationHelper;

import java.io.FileReader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kavinyao on 5/15/14.
 */
public class RandomTuner {
    private static Random R = new Random();

    private static double round(double value) {
        return (double)Math.round(value * 100) / 100;
    }

    private static Map<String, Double> generateRandomConfig(Map<String, double[]> paramRanges) {
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
            System.err.println("Insufficient number of arguments: <bm25|cosine> <signal file> <rel file>");
            return;
        }

        TuningConfig tuningConfig = null;
        String scorerID = args[0];
        if (scorerID.equals("bm25")) {
            tuningConfig = new BM25TuningConfig();
        } else if (scorerID.equals("cosine")) {
            tuningConfig = new CosineTuningConfig();
        } else {
            throw new IllegalArgumentException("Unknown scorer specifier: " + scorerID);
        }

        // make it synchronized as it'll be accessed by more than one thread
        List<Pair<Map<String, Double>, Double>> goodConfigs =
                Collections.synchronizedList(new ArrayList<>());
        final String resultFormatString = tuningConfig.getResultFormatString();

        // save good config upon shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                long unixTime = System.currentTimeMillis() / 1000L;
                String fileName = String.format(resultFormatString, unixTime);
                SerializationHelper.saveObjectToFile(goodConfigs, fileName);
            }
        });

        // load IDF
        IDF idfs = LoadHandler.loadIDFs();

        // init NDCG
        FileReader relFileReader = new FileReader(args[2]);
        NDCG.loadRelScores(relFileReader);

        // load train data
        Map<Query, Map<String, Document>> queryDict = LoadHandler.loadTrainData(args[1]);

        // create scorer
        AScorer scorer = null;
        if (scorerID.equals("bm25")) {
            scorer = new BM25Scorer(idfs, queryDict);
        } else if (scorerID.equals("cosine")) {
            scorer = new CosineSimilarityScorer(idfs);
        }

        Set<String> uniqeConfigs = new HashSet<>();

        Map<String, double[]> paramRanges = tuningConfig.getParamRanges();
        final double threshold = tuningConfig.getNDCGThreshold();
        final double thresholdEx = tuningConfig.getNDCGThresholdEx();

        // run forever
        while (true) {
            // generate random config
            Map<String, Double> config = generateRandomConfig(paramRanges);
            String configString = readableConfig(config);

            // avoid duplicate
            if (uniqeConfigs.contains(configString)) {
                continue;
            }
            uniqeConfigs.add(configString);

            // load custom config
            StringReader stringReader = new StringReader(configString);
            ConfigLoader.setParameters(scorer, stringReader);

            // score documents for queries
            Map<Query, List<String>> queryRankings = Rank.score(queryDict, scorer, idfs);

            // evaluate result
            String s = generateResultString(queryRankings);
            double ndcg = NDCG.computeNDCG(new StringReader(s));

            // save good config
            if (ndcg > threshold) {
                if (ndcg > thresholdEx) {
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
