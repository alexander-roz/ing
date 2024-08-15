package com.unosoft.lng;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataSorting {
    private final File file;
    private ArrayList<String> dataArray = new ArrayList<>();
    @Getter
    private final ConcurrentHashMap<Integer, Set<String>> groups = new ConcurrentHashMap<>();
    @Getter
    private List<Integer> sortedGroupsID;

    public DataSorting(File dataFile) throws IOException {
        System.out.println("-> DataSorting operation started");

        this.file = dataFile;
        this.dataArray = getDataFromFile(dataFile);
        processStrings(dataArray);
        sortedGroupsID = new ArrayList<>(groups.keySet());
        Collections.sort(sortedGroupsID);
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

    private void processStrings(List<String> strings) {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        int partitionSize = (int) Math.ceil((double) strings.size() / availableThreads);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < strings.size(); i += partitionSize) {
            final int start = i;
            final int end = Math.min(start + partitionSize, strings.size());
            tasks.add(() -> {
                processPartition(strings.subList(start, end));
                return null;
            });
        }

        ExecutorService executor = Executors.newFixedThreadPool(availableThreads);
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processing was interrupted: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private void processPartition(List<String> partition) {
        Map<String, Integer> localGroups = new HashMap<>();
        AtomicInteger id = new AtomicInteger(1);

        for (String current : partition) {
            if (!checkTheLine(current)) {
                continue;
            }
            for (String other : partition) {
                if (!checkTheLine(other) || current.equals(other)) {
                    continue;
                }
                if (sameGroupString(current, other)) {
                    localGroups.computeIfAbsent(current, k -> id.getAndIncrement());
                    localGroups.computeIfAbsent(other, k -> id.getAndIncrement());
                }
            }
        }

        for (Map.Entry<String, Integer> entry : localGroups.entrySet()) {
            groups.computeIfAbsent(entry.getValue(), k -> ConcurrentHashMap.newKeySet()).add(entry.getKey());
        }
    }

    private boolean checkTheLine(String line) {
        return line != null && !line.trim().isEmpty();
    }

    private boolean sameGroupString(String string1, String string2) {
        String[] string1Parts = string1.split(";");
        String[] string2Parts = string2.split(";");

        for (String part1 : string1Parts) {
            if ("\"\"".equals(part1)) continue; // Skip empty parts
            for (String part2 : string2Parts) {
                if ("\"\"".equals(part2)) continue; // Skip empty parts
                if (part1.equals(part2)) {
                    System.out.println("Found same parts: " + part1);
                    return true;
                }
            }
        }
        return false;
    }
}
