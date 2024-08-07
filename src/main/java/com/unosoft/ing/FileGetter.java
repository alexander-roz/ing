package com.unosoft.ing;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

@Getter
public class FileGetter {
    private static final String fileName = "src/main/resources/data/";

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
        File file = new File(String.valueOf(findFile(fileName)));
        if(file.getName().endsWith(".gz")) {
            decompress(file);
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

    static void decompress(File input) {
        byte[] buffer = new byte[1024];

        try {
            File decompressed = new File("src/main/resources/data/".concat((input.getName()).substring(0,input.getName().length()-3)));
            Files.createFile(decompressed.toPath());
            System.out.println("Name of decompressed file: " + decompressed.getAbsolutePath());

            FileInputStream fileIn = new FileInputStream(input);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
            FileOutputStream fileOutputStream = new FileOutputStream(decompressed);
            int bytes_read;
            while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes_read);
            }
            gZIPInputStream.close();
            fileOutputStream.close();
            System.out.println("The file was decompressed successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static File findFile(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Directory not found");
            return null;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("No files in directory");
            return null;
        }

        for (File file : files) {
            if (file.isFile()) {
                return file;
            }
        }

        System.out.println("No files found in directory");
        return null;
    }
}
