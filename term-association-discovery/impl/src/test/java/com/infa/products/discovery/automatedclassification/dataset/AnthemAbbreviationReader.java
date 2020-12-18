package com.infa.products.discovery.automatedclassification.dataset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AnthemAbbreviationReader {
	
	private static final String SAMPLE_CSV_FILE_PATH = "src/test/resources/sampledata/Anthem_Abbreviations_Analyst.csv";

	private static final String SYNONYMS_FILE_PATH = "src/test/resources/sampledata/anthem_synonyms.txt";

	public static Map<String, List<String>> getAbbreviations() throws IOException {
		Map<String, List<String>> abbrMap = new HashMap<>();
		try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
			boolean first = true;
			for (CSVRecord csvRecord : csvParser) {
				if (first) {
					first = false;
					continue;
				}
				// Accessing Values by Column Index
				String abbr = csvRecord.get(1);
				String desc = csvRecord.get(4);
				abbr.trim();
				desc.trim();

				System.out.println("Record No - " + csvRecord.getRecordNumber());
				System.out.println("---------------");
				System.out.println(abbr.toUpperCase() + " :: " + desc);
				System.out.println("---------------\n\n");
				if (!abbr.trim().isEmpty()) {
					abbrMap.put(abbr.toUpperCase(), new ArrayList<>());
					if (desc.contains(" or ")) {
						String[] splits = desc.split(" or ");
						for (String split : splits) {
							abbrMap.get(abbr.toUpperCase()).add(split);
						}
					} else {
						abbrMap.get(abbr.toUpperCase()).add(desc);
					}
				}

			}
		}
		System.out.println("Abbreviations read: " + abbrMap.size());
		return abbrMap;
	}
	
	public static void writeUsingBufferedWriter(String data, int noOfLines) {
		OutputStream os = null;
        try {
            os = new FileOutputStream(new File(SYNONYMS_FILE_PATH));
            os.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}