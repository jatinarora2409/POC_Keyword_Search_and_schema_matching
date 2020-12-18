package com.infa.products.discovery.automatedclassification.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static void clearContentsOfFile(File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
    }

    public static void clearContentsOfFile(String filePath) throws IOException {
        clearContentsOfFile(new File(filePath));
    }

    public static boolean isFileExists(File file) {
        if (file != null) {
            return file.exists();
        } else {
            return false;
        }
    }

    public static boolean isFileExists(String filePath) {
        return isFileExists(new File(filePath));
    }
}

