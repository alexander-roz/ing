package com.unosoft.lng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class IngApplication {

    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        String path;
        SpringApplication.run(IngApplication.class, args);
//		if(args[0].isBlank()) {
//			path = args[0];
//		}
//		else{
        path = "https://github.com/PeacockTeam/new-job/releases/download/v1.0/lng-4.txt.gz";
//		}

        FileExtractor fileExtractor = new FileExtractor(path);
        DataSorting dataSorting = new DataSorting(fileExtractor.getDataFile());
//        HashMap <Integer, Set<String>> sortedGroups = dataSorting.getGroups();

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Programming time: " + duration.toSeconds() + " sec");
    }

}
