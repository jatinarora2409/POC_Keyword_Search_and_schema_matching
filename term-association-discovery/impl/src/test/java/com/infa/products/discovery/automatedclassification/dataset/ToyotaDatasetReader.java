package com.infa.products.discovery.automatedclassification.dataset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ToyotaDatasetReader {

    private static final String TOYOTA_CSV_FILE_PATH = "src/test/resources/sampledata/ToyotaBGExport.csv";
    private Set<String> terms = new HashSet<>();

    private Map<String, String> colIdBtMap = new HashMap<>();

    private Map<String, String> columnIdNameMap = new HashMap<>();

    private Set<String> colsWithoutBt = new HashSet<>();

    private Set<String> unique = new HashSet<>();

    public void read() throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(TOYOTA_CSV_FILE_PATH));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
            boolean first = true;
            for (CSVRecord csvRecord : csvParser) {
                if (first) {
                    first = false;
                    continue;
                }
                // Accessing Values by Column Index
                String bt = csvRecord.get(0);
//				String bt = csvRecord.get(1);
                String cols = csvRecord.get(1);
//				String cols = csvRecord.get(0)
                String term = bt.trim();
                cols = cols.replaceAll("\"", "");
                if (!term.isEmpty()) {
                    if(unique.contains(cols))
                        continue;
                    unique.add(cols);
                    System.out.println("Record No - " + csvRecord.getRecordNumber());
                    System.out.println("---------------");
                    System.out.println("Term : " + term);
                    terms.add(term);
                    if(cols.contains(",") && !cols.trim().isEmpty()) {
                        String[] splits = cols.split(",");
                        for(String split : splits) {
                            String fqColName = split.trim();
                            String colName = fqColName.substring(fqColName.lastIndexOf(".") + 1);

                            if(colName.trim().isEmpty()) {
                                continue;
                            }

                            colIdBtMap.put(colName.trim(), bt);
                            columnIdNameMap.put(colName.trim(), colName.trim());
                        }
                    } else {
                        String fqColName = cols.trim();
                        String colName = fqColName.substring(fqColName.lastIndexOf(".") + 1);
                        if(colName.trim().isEmpty()) {
                            continue;
                        }

                        colIdBtMap.put(colName.trim(), bt);
                        columnIdNameMap.put(colName.trim(), colName.trim());
                    }
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
