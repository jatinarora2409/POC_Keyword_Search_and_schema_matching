package com.infa.products.discovery.automatedclassification.dataset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AnthemColsDatasetReader {

    private final String anthemColsCSVFilePath;

    private Set<String> terms = new HashSet<>();

    private Map<String, String> colIdBtMap = new HashMap<>();

    private Map<String, String> columnIdNameMap = new HashMap<>();

    private Set<String> colsWithoutBt = new HashSet<>();

    private Set<String> unique = new HashSet<>();
    public AnthemColsDatasetReader(String anthemColsCSVFilePath) {
        this.anthemColsCSVFilePath = anthemColsCSVFilePath;
    }

    public void read() throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(anthemColsCSVFilePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
            boolean first = true;
            for (CSVRecord csvRecord : csvParser) {
                if (first) {
                    first = false;
                    continue;
                }
                // Accessing Values by Column Index
                String id = csvRecord.get(0);
                String name = csvRecord.get(1);
                String classType = csvRecord.get(2);
                String term = csvRecord.get(3);

                String bt = term.replace("/EDWard Glossary/", "");

                System.out.println("Record No - " + csvRecord.getRecordNumber());
                System.out.println("---------------");
                System.out.println("Term : " + bt);
                System.out.println("---------------\n\n");
                if (!bt.trim().isEmpty()) {

                    if(unique.contains(name))
                        continue;
                    unique.add(name);
                    terms.add(bt);
                    colIdBtMap.put(id, bt);
                    columnIdNameMap.put(id, name);
                } else {
                    colsWithoutBt.add(id);
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
