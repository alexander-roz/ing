package com.unosoft.lng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class IngApplication {

    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        String path;
        SpringApplication.run(IngApplication.class, args);
        if (args.length == 0) {
            path = "https://github.com/PeacockTeam/new-job/releases/download/v1.0/lng-4.txt.gz";
        } else {
            path = args[0];
        }

        FileExtractor fileExtractor = new FileExtractor(path);
        DataSorting dataSorting = new DataSorting(fileExtractor.getDataFile());
        HashMap<Integer, Set<String>> data = dataSorting.getGroups();
        List<Integer> sortedGroupIDs = dataSorting.getSortedGroupsID();

        int counter = 0; //вывод на экран ограничен 20 группами с наибольшим количеством строк
        for (Integer groupID : sortedGroupIDs) {
            if(counter < 20) {
                System.out.println("> Group ID: " + groupID + " with " + data.get(groupID).size() + " lines:");
                counter++;
//            data.get(groupID).forEach(System.out::println);
            }
            else break;
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Programming time: " + duration.toSeconds() + " sec");
    }

}
