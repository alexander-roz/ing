package com.unosoft.ing;

import lombok.Getter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class FileGetter {
    private final String fileName = "./src/main/resources/data/";

    public FileGetter(String path) throws IOException {
        if(path != null) {
            extractFile(path);
        }
        else System.out.println("Invalid path");
    }

    public void extractFile(String path) throws IOException {
        if(path.startsWith("https://") || path.startsWith("http://")) {
            getFileFromURL(path);
        }
        else {
           if(Files.exists(Path.of(path))) {
                getFileFromPath(path);
            }
        }
    }

    private void getFileFromURL(String path) throws MalformedURLException {
        URL url = new URL(path);

        try {
            File file = new File(fileName.concat(url.getPath().substring(url.getPath().lastIndexOf("/") + 1)));
            if (file.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File already exists");
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    private void getFileFromPath(String path) throws IOException {
        File existingFile = new File(path);
        if(existingFile.exists()) {
            if(Files.exists(Path.of(fileName.concat(existingFile.getName())))) {
                System.out.println("File already exists");
            }
            else {
                Files.copy(Path.of(existingFile.getPath()), Path.of(fileName.concat(existingFile.getName())));
                System.out.println("File copied");
            }
        }
        else System.out.println("File does not exist");

    }
}
