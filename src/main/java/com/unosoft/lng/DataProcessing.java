package com.unosoft.lng;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DataProcessing {
    private File file;
    private ArrayList<String> dataArray = new ArrayList<>();
    private HashMap<Integer, String> groups = new HashMap<>();

    public DataProcessing(File file) {
        this.file = file;
    }

    public ArrayList<String> getDataFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while (line != null) {
                line = reader.readLine();
                dataArray.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataArray;
    }

}
