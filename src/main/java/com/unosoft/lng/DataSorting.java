package com.unosoft.lng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DataSorting {
    private ArrayList<String> dataArray;
    public DataSorting(ArrayList<String> dataArray) {
        this.dataArray = dataArray;
    }
    private HashMap<Integer, Set<String>> sortArray(ArrayList<String> dataArray){
        HashMap<Integer, Set<String>> sortedData = new HashMap<>();
        for (int i = 0; i < dataArray.size(); i++) {

        }
        return sortedData;
    }

    public boolean checkTheLine(String line) {
        boolean correct = true;

        if(line == null) {
            correct = false;
        }
        else {
            String[] numbers = line.split(";");
            for (String number : numbers) {
                if (!number.matches("\"[0-9]*\"")) {
                    System.out.println("wrong line found: " + line);
                    correct = false;
                    break;
                }
            }
        }
        return correct;
    }
}
