package com.unosoft.lng;

import lombok.Getter;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataProcessing {
    @Getter
    private File file;

    public DataProcessing(File file) {
        this.file = file;
    }

    public Set<String> getDataFromFile() throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        Set<String> dataSet = new HashSet<String>();
        String line = "";
        while (line != null) {
            line = bufferedReader.readLine();
            dataSet.add(line);
            System.out.println(line);
        }
        return dataSet;
    }


}
