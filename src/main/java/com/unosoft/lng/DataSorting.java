package com.unosoft.lng;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        this.groups = groupToMapByArray(dataArray);
        this.sortedGroupsID = sortTheMap(groups);
    }

    private HashMap<Integer, Set<String>> groupToMapByArray(ArrayList<String> dataArray) {
        System.out.println("-> sortArray() Trying to sort data array");
        int wrongLines = 0;
        // Приведем к виду Map элемента к ID группы
        Map<String, Integer> lastGroupId = new HashMap<>();
        int currentGroupId = 0;
        // Проход по всем строкам
        for (String str : dataArray) {
            if (!checkTheLine(str)) {
                wrongLines++;
                continue;
            }
            // Разделяем строку на части
            String[] parts = str.split(";");
            Set<Integer> groupIds = new HashSet<>();
            // Смотрим, в каких группах уже есть элементы
            for (String part : parts) {
                if (lastGroupId.containsKey(part)) {
                    groupIds.add(lastGroupId.get(part)); // добавляем ID группы
                }
            }
            // Если есть ID групп, добавляем в них
            if (!groupIds.isEmpty()) {
                // Берем одну из групп для текущей строки
                int assignedGroupId = groupIds.iterator().next();
                groups.get(assignedGroupId).add(str); // добавляем строку в уже существующую группу
                // Обновляем соответствие строк и групп
                for (String part : parts) {
                    lastGroupId.put(part, assignedGroupId);
                }
            } else {
                // Создаем новую группу
                groups.put(currentGroupId, new HashSet<>(Collections.singletonList(str)));
                for (String part : parts) {
                    lastGroupId.put(part, currentGroupId);
                }
                currentGroupId++;
            }
        }

        int count = 0;
        for (Map.Entry<Integer, Set<String>> entry : groups.entrySet()) {
            count = count + entry.getValue().size();
        }
        System.out.println("After sorting " + groups.size() + " groups with " + count + " lines");
        System.out.println("Wrong lines found and passed: " + wrongLines);

        return groups;
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
}
