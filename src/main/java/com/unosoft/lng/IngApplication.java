package com.unosoft.lng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootApplication
public class IngApplication {

	public static void main(String[] args) throws IOException {
		String path;
		SpringApplication.run(IngApplication.class, args);
//		if(args[0].isBlank()) {
//			path = args[0];
//		}
//		else{
			path= "https://github.com/PeacockTeam/new-job/releases/download/v1.0/lng-4.txt.gz";
//			path = "C:\\Users\\test\\Desktop\\lng-4.txt.gz";
//		}

		FileExtractor fileExtractor = new FileExtractor(path);
		DataProcessing dataProcessing = new DataProcessing(fileExtractor.getDataFile());
		ArrayList<String> dataArray = dataProcessing.getDataFromFile();
		DataSorting dataSorting = new DataSorting(dataArray);
		for(String line:dataArray){
			dataSorting.checkTheLine(line);
		}

	}
}
