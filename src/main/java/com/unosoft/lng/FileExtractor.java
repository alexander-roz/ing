package com.unosoft.lng;

import lombok.Getter;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileExtractor {
    private static final String fileDir = "src/main/resources/data/";
    @Getter
    private File dataFile;

    public FileExtractor(String path) throws IOException {
        if (path != null) {
            extractFile(path);
        } else System.out.println("Invalid path, please check input data");
    }

    private void extractFile(String path) throws IOException {
        if (path.startsWith("https://") || path.startsWith("http://")) {
            getFileFromURL(path);
        } else {
            if (Files.exists(Path.of(path))) {
                getFileFromPath(path);
            }
        }
        File file = new File(String.valueOf(findFile(fileDir)));
        System.out.println("File name in directory: " + file.getAbsolutePath());

        if (file.getName().endsWith("gz")) {
            dataFile = decompress(Objects.requireNonNull(file));
        } else {
            dataFile = file;
        }
    }

    private void getFileFromURL(String path) {
        try {
            URL url = new URL(path);
            InputStream in = url.openStream();
            File dir = new File(fileDir);
            dir.mkdirs();
            String fileName = getFileName(url);
            FileOutputStream out = new FileOutputStream(fileDir + File.separator + fileName);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            in.close();
            System.out.println("File downloaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(URL url) {
        String[] parts = url.getFile().split("/");
        return parts[parts.length - 1];
    }

    private void getFileFromPath(String path) throws IOException {
        File existingFile = new File(path);
        if (existingFile.exists()) {
            if (Files.exists(Path.of(fileDir.concat(existingFile.getName())))) {
                System.out.println("File already exists");
            } else {
                Files.copy(Path.of(existingFile.getPath()), Path.of(fileDir.concat(existingFile.getName())));
                System.out.println("File copied");
            }
        } else System.out.println("File does not exist");
    }

    private static File decompress(File input) throws IOException {
        File file = null;
        // Открываем входной файл
        GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(input)));
        // Получаем имя выходного файла
        String outputFileName = String.valueOf(input.getPath()).substring(0, input.getPath().length() - 3);
        // Создаем выходной файл
        File outputFile = new File(outputFileName);
        FileOutputStream fos = new FileOutputStream(outputFileName);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        // Закрываем потоки
        fos.close();
        gis.close();
        System.out.println("the archive's been successfully unpacked " + outputFileName);
        file = outputFile;
        return file;
    }

    public static File findFile(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("the directory was not found");
            return null;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("there are no files in " + directoryPath);
            return null;
        }
        for (File file : files) {
            if (file.isFile()) {
                return file;
            }
        }
        System.out.println("there are no files in " + directoryPath);
        return null;
    }
}
