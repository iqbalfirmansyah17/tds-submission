package com.telkomdigitalsolution.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 *
 * @author iqbalfirmansyah
 */
@Command(name = "submission", description = "Performs log file manipulation operations.", mixinStandardHelpOptions = true, version = "Submission Telkom Digital Solution 1.0")
public class Main implements Runnable {

    @Option(names = "-i", description = "Path where the input file is located, default path '/var/log/system.log'.")
    private String inputFilePath = "/var/log/system.log";

    @Option(names = "-o", description = "Path where the output you want to extract as a file (optional), default value is same with input path.")
    private String outputFilePath;

    @Option(names = "-t", description = "Type text of output 'plain' or 'json', default type is 'plain'.")
    private String typeOutput = "plain";

    private String result;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if (typeOutput.equals("json")) {
            if (outputFilePath == null) {
                System.out.println(readLogJson(inputFilePath));
            } else {
                createFile(inputFilePath, outputFilePath, "json");
            }
        } else {
            if (outputFilePath == null) {
                System.out.println(readLogPlain(inputFilePath));
            } else {
                createFile(inputFilePath, outputFilePath, "plain");
            }
        }
    }

    public String readLogPlain(String fileLocation) {
        try {
            try (FileInputStream fstream = new FileInputStream(fileLocation)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    result += strLine + "\n";
                }
                result = result.substring(0, result.length() - 2);
            }
            return result;
        } catch (IOException e) {
            return "Failed read the input file.";
        }
    }

    public String readLogJson(String fileLocation) {
        try {
            try (FileInputStream fstream = new FileInputStream(fileLocation)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                result = "{\n";
                long count = 1;
                while ((strLine = br.readLine()) != null) {
                    result += "\"line " + count + "\" : \"" + strLine + "\",\n";
                    count++;
                }
                result = result.substring(0, result.length() - 2);
                result += "\n}";
            }
            return result;
        } catch (IOException e) {
            return "Failed read the input file.";
        }
    }

    public void createFile(String fileInPath, String fileOutPath, String typeFile) {
        try {
            if (typeFile.equals("json")) {
                result = readLogJson(fileInPath);
            } else {
                result = readLogPlain(fileInPath);
            }
            File myObj = new File(fileOutPath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                writeToFile(fileOutPath, result);
            } else {
                Scanner input = new Scanner(System.in);
                System.out.println("File already exists, do you want to overwrite contents? [Y/N]");
                String choice = input.nextLine();
                if (choice.equalsIgnoreCase("Y")) {
                    writeToFile(fileOutPath, result);
                } else if (choice.equalsIgnoreCase("N"))  {
                    System.out.println("Cancel wrote to the file.");
                } else {
                    System.out.println("Wrong choice, operation canceled.");
                }
            }
        } catch (IOException e) {
            System.out.println("Failed create file.");
        }
    }

    public void writeToFile(String filePath, String data) {
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully write contents to the file.");
        } catch (IOException e) {
            System.out.println("Failed write contents to the file.");
        }
    }
}
