package edu.stanford.cs276;

import java.io.*;
import java.util.*;

public class NDCG {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Not enough arguments!");
            System.exit(1);
        }

        HashMap<Integer, Double> relScores = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(args[1]));
        String strLine;
        String query = "";
        while ((strLine = br.readLine()) != null) {
            if (strLine.trim().charAt(0) == 'q') {
                query = strLine.substring(strLine.indexOf(":")+1).trim();
            } else {
                String[] tokens = strLine.substring(strLine.indexOf(":")+1).trim().split(" ");
                String url = tokens[0].trim();
                double relevance = Double.parseDouble(tokens[1]);
                if (relevance < 0) {
                    relevance = 0;
                }
                relScores.put(query.hashCode() + url.hashCode(), relevance);
            }
        }
        br.close();

        if (args[0].equals("-")) {
            br = new BufferedReader(new InputStreamReader(System.in));
        } else {
            br = new BufferedReader(new FileReader(args[0]));
        }

        int totalQueries = 0;
        ArrayList<Double> rels = new ArrayList<>();
        double totalSum = 0;
        int success = 0, failure = 0;

        while ((strLine = br.readLine()) != null) {
            if (strLine.trim().charAt(0) == 'q') {
                if (rels.size() > 0) {
                    totalSum = getNDCGQuery(rels, totalSum);
                    rels.clear();
                }
                query = strLine.substring(strLine.indexOf(":")+1).trim();
                totalQueries++;
            } else {
                String url = strLine.substring(strLine.indexOf(":")+1).trim();
                if (relScores.containsKey(query.hashCode() + url.hashCode())) {
                    double relevance = relScores.get(query.hashCode() + url.hashCode());
                    rels.add(relevance);
                    ++success;
                } else {
                    System.err.printf("Warning. Cannot find query %s with url %s in %s. Ignoring this line.\n", query, url, args[1]);
                    ++failure;
                }
            }
        }

        br.close();

        if (rels.size() > 0) {
            totalSum = getNDCGQuery(rels, totalSum);
        }

        System.err.println("Relevant URLs: " + success);
        System.err.println("Non-relevant URLS: " + failure);
        System.out.println(totalSum/totalQueries);
    }

    private static double getNDCGQuery(ArrayList<Double> rels, double totalSum) {
        double localSum = 0, sortedSum = 0;

        for (int i = 0; i < rels.size(); i++) {
            localSum += (Math.pow(2, rels.get(i)) - 1) / (Math.log(1 + i + 1) / Math.log(2));
        }

        Collections.sort(rels, Collections.reverseOrder());

        for (int i = 0; i < rels.size(); i++) {
            sortedSum += (Math.pow(2, rels.get(i)) - 1) / (Math.log(1 + i + 1) / Math.log(2));
        }

        if (sortedSum == 0) {
            totalSum += 1;
        } else {
            totalSum += localSum / sortedSum;
        }

        return totalSum;
    }
}
