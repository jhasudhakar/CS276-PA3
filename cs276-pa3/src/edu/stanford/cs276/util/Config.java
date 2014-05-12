package edu.stanford.cs276.util;

import edu.stanford.cs276.doc.DocField;
import edu.stanford.cs276.scorer.AScorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiwei on 5/12/14.
 */
public class Config {
    public static void setParameters(AScorer scorer, String fileName) {
        BufferedReader br = null;
        Field[] fields = scorer.getClass().getDeclaredFields();
        /* String -> DocField. */
        Map<String, DocField> stringDocFieldMap = new HashMap<>();
        for (DocField docfield : DocField.values()) {
            stringDocFieldMap.put(docfield.name(), docfield);
        }
        /* String -> Field. */
        Map<String, Field> stringFieldMap = new HashMap<>();
        for (Field field : fields) {
            stringFieldMap.put(field.getName(), field);
        }
        try {
            File file = new File(fileName);
            br = new BufferedReader(new FileReader(file));
            String line;
            /* Process one line at a time. */
            while ((line = br.readLine()) != null) {
                int indexPeriod = line.indexOf('#');
                int indexEqual = line.indexOf('=');
                double value = Double.parseDouble(line.substring(indexEqual + 1));
                if (indexPeriod != -1) {
                    // If token[0] contains '.' like in "Wf.url".
                    try {
                        String fieldName = line.substring(0, indexPeriod);
                        String key = line.substring(indexPeriod + 1, indexEqual);
                        // Access a specific field
                        Field field = stringFieldMap.get(fieldName);
                        field.setAccessible(true);
                        Map<DocField, Double> map = (Map<DocField, Double>)(field.get(scorer));
                        // Invoke put method
                        map.put(stringDocFieldMap.get(key), value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // For example, "K1=10".
                    try {
                        String fieldName = line.substring(0, indexEqual);
                        Field field = stringFieldMap.get(fieldName);
                        field.setAccessible(true);
                        field.setDouble(scorer, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}