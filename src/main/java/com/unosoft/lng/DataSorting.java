package com.unosoft.lng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DataSorting {
    private ArrayList<String> dataArray;

    public DataSorting(ArrayList<String> dataArray) {
        System.out.println("DataSorting constructor started");
        this.dataArray = dataArray;
    }

    private HashMap<Integer, Set<String>> sortArray() {
        HashMap<Integer, Set<String>> sortedData = new HashMap<>();
        for(String line : dataArray) {

        }
        return sortedData;
    }

    public boolean checkTheLine(String line) {
        boolean correct = true;
        String regex = "\"[0-9]*\"";
        if (line == null || !line.startsWith("\"")) {
            correct = false;
            return correct;
        }
        if (line.contains(";")) {
            String[] numbers = line.split(";");
            for (String number : numbers) {
                if (!number.matches(regex)) {
                    correct = false;
                    break;
                }
                return correct;
            }
        } else {
            if (!line.matches(regex)) {
                correct = false;
            }
        }
        return correct;
    }
}
