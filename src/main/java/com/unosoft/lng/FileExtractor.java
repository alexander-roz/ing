package com.unosoft.lng;

import lombok.Getter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileExtractor {
//    private static final String fileDir = "src/main/resources/data/";
    private static final String desktop = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;
    private static String fileDir;
    @Getter
    private File dataFile;

    public FileExtractor(String path) throws IOException {
        if (path != null) {
            String workDir = desktop + "lng_app_data";
            new File(workDir).mkdirs();
            fileDir = workDir;
            System.out.println("Directory for project files: " + desktop) ;
            extractFile(path);
        } else System.out.println("Invalid path, please check input data");
    }

    //метод для работы с полученным в качестве аргумента параметром
    private void extractFile(String path) throws IOException {
        System.out.println("-> extractFile() Extracting file: " + path);
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

    //метод получения данных, в случае если входной параметр - ссылка
    private void getFileFromURL(String path) throws MalformedURLException {
        System.out.println("-> getFileFromURL() Trying to get file from URL: " + path);
        URL url = new URL(path);
        String fileName = getFileName(path);
        Path outputPath = Path.of(fileDir + File.separator + fileName);

        System.out.println("-> Operating with file: " + outputPath);
        try (InputStream in = url.openStream()) {
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //метод отделения имени файла от абсолютного пути
    private String getFileName(String path) {
        System.out.println("-> getFileName() Trying to get file name: " + path);
        String[] parts = path.split("/");
        System.out.println("file name: " + parts[parts.length - 1]);
        return parts[parts.length - 1];
    }

    //метод получения данных, в случае если входной параметр - путь
    private void getFileFromPath(String path) throws IOException {
        System.out.println("-> getFileFromPath() Trying to get file from path: " + path);
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

    //метод для распаковки файлов .gz
    private static File decompress(File input) throws IOException {
        System.out.println("-> decompress() Trying to decompress file: " + input);
        File file = null;
        GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(input)));
        String outputFileName = String.valueOf(input.getPath()).substring(0, input.getPath().length() - 3);
        File outputFile = new File(outputFileName);
        FileOutputStream fos = new FileOutputStream(outputFileName);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        gis.close();
        System.out.println("the archive's been successfully unpacked " + outputFileName);
        file = outputFile;
        return file;
    }

    //метод для поиска файлов в указанной директории
    public static File findFile(String directoryPath) {
        System.out.println("-> findFile() Trying to find file in: " + directoryPath);
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
                System.out.println("found file: " + file.getAbsolutePath());
                return file;
            }
        }
        System.out.println("there are no files in " + directoryPath);
        return null;
    }
}
