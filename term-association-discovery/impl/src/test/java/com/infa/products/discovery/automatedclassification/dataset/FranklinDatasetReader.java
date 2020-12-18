package com.infa.products.discovery.automatedclassification.dataset;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FranklinDatasetReader {

    private static final String FRANKLIN_CSV_FILE_PATH = "src/test/resources/sampledata/franklin.csv";

    private Set<String> terms = new HashSet<>();

    private Map<String, String> colIdBtMap = new HashMap<>();

    private Map<String, String> columnIdNameMap = new HashMap<>();

    private Set<String> colsWithoutBt = new HashSet<>();

    private Set<String> unique = new HashSet<>();

    public void read() throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(FRANKLIN_CSV_FILE_PATH));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
            boolean first = true;
            for (CSVRecord csvRecord : csvParser) {
                if (first) {
                    first = false;
                    continue;
                }
                // Accessing Values by Column Index
                String asset = csvRecord.get(0);

                String term = csvRecord.get(2);

                System.out.println("Record No - " + csvRecord.getRecordNumber());
                System.out.println("---------------");
                System.out.println("Term : " + term);
                System.out.println("---------------\n\n");
                if (!term.trim().isEmpty()) {
                    if(unique.contains(asset))
                        continue;
                    unique.add(asset);
                    terms.add(term);
                    colIdBtMap.put(asset, term);
                    columnIdNameMap.put(asset, asset);
                } else {
                    colsWithoutBt.add(asset);
                }

            }
        }
        System.out.println("Terms read: " + terms.size());
    }

    public List<String> getBussinessGlossaryTerms() {
        return new ArrayList<>(terms);
    }

    public Map<String, String> getColumnNames() {
        return columnIdNameMap;
    }

    public String getAssociatedBusinessTerm(String assetIdentifier) {
        if (assetIdentifier == null) {
            throw new NullPointerException("Column has no BT associated with it.");
        }
        return colIdBtMap.get(assetIdentifier);
    }

}
