package com.project.apdesktopapplication.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String RESOURCES_FILE = DATA_DIR + "resources.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.txt";

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created data directory: " + DATA_DIR);
        }
    }

    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filename);
            return lines;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            System.out.println("Read " + lines.size() + " lines from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeFile(String filename, List<String> lines) {
        File file = new File(filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Wrote " + lines.size() + " lines to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readUsers() {
        return readFile(USERS_FILE);
    }

    public static void writeUsers(List<String> lines) {
        writeFile(USERS_FILE, lines);
    }

    public static List<String> readResources() {
        return readFile(RESOURCES_FILE);
    }

    public static void writeResources(List<String> lines) {
        writeFile(RESOURCES_FILE, lines);
    }

    public static List<String> readBookings() {
        return readFile(BOOKINGS_FILE);
    }

    public static void writeBookings(List<String> lines) {
        writeFile(BOOKINGS_FILE, lines);
    }
}