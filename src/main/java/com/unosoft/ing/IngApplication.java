package com.unosoft.ing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

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

		FileGetter fileGetter = new FileGetter(path);
	}
}
