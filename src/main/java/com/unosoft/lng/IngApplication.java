package com.unosoft.lng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

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
        DataProcessing dataProcessing = new DataProcessing(fileExtractor.getDataFile());
        ArrayList<String> dataArray = dataProcessing.getDataFromFile();
        DataSorting dataSorting = new DataSorting(dataArray);
        int count = 0;
        int countWrong = 0;
        for (String line : dataArray) {
            if (!dataSorting.checkTheLine(line)) {
                System.out.println("Wrong line: " + line);
                countWrong++;
            }
            count++;
        }
        System.out.println("checked lines: " + count + "\nwrong lines: " + countWrong);

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toSeconds();
        System.out.println("The program execution is completed after: " + elapsed + " sec");
    }
}
