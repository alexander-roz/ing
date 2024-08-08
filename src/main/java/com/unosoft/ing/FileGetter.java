package com.unosoft.ing;

import lombok.Getter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

@Getter
public class FileGetter {
    private static final String fileDir = "src/main/resources/data/";

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
        File file = new File(String.valueOf(findFile(fileDir)));
        System.out.println("File name in directory: " + file.getAbsolutePath());

        if(file.getName().endsWith("gz")) {
            decompress(Objects.requireNonNull(file));
        }
    }

    private void getFileFromURL(String path) throws MalformedURLException {
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
            System.out.println("File downloaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getFileName(URL url) {
        String[] parts = url.getFile().split("/");
        return parts[parts.length - 1];
    }

    private void getFileFromPath(String path) throws IOException {
        File existingFile = new File(path);
        if(existingFile.exists()) {
            if(Files.exists(Path.of(fileDir.concat(existingFile.getName())))) {
                System.out.println("File already exists");
            }
            else {
                Files.copy(Path.of(existingFile.getPath()), Path.of(fileDir.concat(existingFile.getName())));
                System.out.println("File copied");
            }
        }
        else System.out.println("File does not exist");
    }

    static void decompress(File input) throws IOException {

        try {
            // Открываем входной файл
            GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(input)));

            // Получаем имя выходного файла
            String outputFileName = String.valueOf(input.getPath()).substring(0, String.valueOf(input.getPath()).length() - 3); // убираем расширение .gz

            // Создаем выходной файл
            FileOutputStream fos = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            // Закрываем потоки
            fos.close();
            gis.close();

            System.out.println("Архив успешно распакован в файл " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
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
