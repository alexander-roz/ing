package com.unosoft.lng;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataSorting {
    private File file;
    private ArrayList<String> dataArray = new ArrayList<>();
    @Getter
    private HashMap<Integer, Set<String>> groups = new HashMap<>();
    @Getter
    private List<Integer> sortedGroupsID;

    public DataSorting(File dataFile) {
        System.out.println("-> DataSorting operation started");
        this.file = dataFile;
        this.dataArray = getDataFromFile(file);
        StringGroup stringGroup = new StringGroup(dataArray);
        this.groups = stringGroup.groups;
        this.sortedGroupsID = sortTheMap(groups);
    }

    //метод чтения данных из файла и сохранения в ArrayList для дальнейшей сортировки
    public ArrayList<String> getDataFromFile(File file) {
        System.out.println("-> getDataFromFile() Trying to make ArrayList with all lines");
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
        System.out.println("Lines saved into ArrayList before grouping: " + dataArray.size());
        return dataArray;
    }

    //метод сортировки полученных групп по убыванию количества строк, возвращается список с ID групп
    private static List<Integer> sortTheMap(HashMap<Integer, Set<String>> map) {
        System.out.println("-> sortTheMap() Trying to sort data HashMap and return sorted List with IDs");
        List<Integer> topValues = map.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().size()
                        )
                )
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.out.println("After sortTheMap() method structured: " + topValues.size() + " groups");
        return topValues;
    }

    static class StringGroup extends RecursiveTask {
        private final ArrayList<String> strings;
        private HashMap<Integer, Set<String>> groups = new HashMap<>();

        StringGroup(ArrayList<String> dataArray) {
            this.strings = dataArray;
            compute();
        }

        @Override
        protected Object compute() {
            if (strings.size() < 5000) {
                System.out.println("-> groupToMapByArray() Trying to collect strings to groups");
                AtomicInteger id = new AtomicInteger(1);
                for (int i = 0; i < strings.size(); i++) {
                    if (!checkTheLine(strings.get(i)) || strings.get(i) == null) continue;
                    Set<String> group = new TreeSet<>();
                    if(groups.isEmpty()){
                        group.add(strings.get(i));
                        groups.put(id.get(), group);
                        id.incrementAndGet();
                    }
                    for (int j = i + 1; j < strings.size(); j++) {
                        if (!checkTheLine(strings.get(j)) || strings.get(j) == null) continue;
                        if (sameGroupString(strings.get(i), strings.get(j))) {
                            if (groups.containsValue(strings.get(j))) {
                                for (Map.Entry<Integer, Set<String>> set : groups.entrySet()) {
                                    if (set.getValue().contains(strings.get(j))) {
                                        set.getValue().add(strings.get(i));
                                    }
                                }
                            } else {
                                group.add(strings.get(i));
                                group.add(strings.get(j));
                                groups.put(id.get(), group);
                                id.incrementAndGet();
                            }
                        }
                    }
                }
                return groups;
            } else {
                ArrayList<StringGroup> tasks = new ArrayList<>();
                int partitionSize = strings.size()/10;

                Collection<List<String>> partitionedList = IntStream.range(0, strings.size())
                        .boxed()
                        .collect(Collectors.groupingBy(partition -> (partition / partitionSize), Collectors.mapping(elementIndex -> strings.get(elementIndex), Collectors.toList())))
                        .values();

                for (List<String> partOfStringsArray : partitionedList) {
                    StringGroup task = new StringGroup((ArrayList<String>) partOfStringsArray);
                    task.fork();
                    tasks.add(task);
                }
                for (StringGroup task : tasks) {
                    task.join();
                }
            }
            return groups;
        }

        private synchronized boolean sameGroupString(String string1, String string2) {
            boolean same = false;
            String[] string1Parts;
            String[] string2Parts;

            if (!string1.contains(";")) {
                string1Parts = new String[]{string1};
            } else {
                string1Parts = string1.split(";");
            }
            if (!string2.contains(";")) {
                string2Parts = new String[]{string2};
            } else {
                string2Parts = string2.split(";");
            }

            int linit = Math.min(string1Parts.length, string2Parts.length);
            for (int i = 0; i < linit; i++) {
                if (string1Parts[i].equals("\"\"") || string2Parts[i].equals("\"\"")) continue;
                if (string1Parts[i].equals(string2Parts[i])) {
                    same = true;
                    System.out.println("found same parts: " + string2Parts[i] + " & " + string1Parts[i]);
                }
            }
            return same;
        }

        //метод проверки строки на соответствие заданным условиям
        public synchronized boolean checkTheLine(String line) {
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
}
