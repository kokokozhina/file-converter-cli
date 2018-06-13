package com.kokokozhina.cli;

import com.kokokozhina.converter.ConverterXml;
import com.kokokozhina.handler.Handler;
import com.kokokozhina.converter.DefaultConverter;
import com.kokokozhina.task.Task;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Cli {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        boolean exit = false;

        Handler handler = new Handler();

        while (!exit) {
            System.out.println(Messages.INTRO);

            int num = -1;
            boolean canGoNext = true;
            try {
                num = (new Scanner(System.in)).nextInt();

                if (num < 0 || num >= Messages.cnt) {
                    throw new InputMismatchException();
                }

            } catch (InputMismatchException ex) {
                canGoNext = false;
                System.out.println(Messages.WRONG_INPUT);
            }

            if (canGoNext) {
                if (num == 0) {
                    exit = true;
                } else {

                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    boolean tryMore;


                    do {
                        System.out.println(Messages.INPUT_FILE_PATH);

                        tryMore = false;

                        String inputFilePath = (new Scanner(System.in)).nextLine();
                        File inputFile = new File(inputFilePath);
                        if (inputFile.isFile() && inputFile.canRead()) {
                            inputStream = new FileInputStream(inputFilePath);
                        } else {
                            System.out.println(Messages.INPUT_FILE_NOT_FOUND);
                            inputStream = null;

                            System.out.println(Messages.TRY_ONE_MORE_TIME);

                            if (Messages.YES.equals(new Scanner(System.in).nextLine())) {
                                tryMore = true;
                            }
                        }
                    }
                    while (tryMore);

                    if (inputStream != null) {
                        do {
                            System.out.println(Messages.OUTPUT_FILE_PATH);

                            tryMore = false;

                            String outputFilePath = (new Scanner(System.in)).nextLine();
                            File outputFile = new File(outputFilePath);
                            if (outputFile.isFile() && outputFile.canWrite()) {
                                outputStream = new FileOutputStream(outputFilePath);
                            } else {
                                System.out.println(Messages.OUTPUT_FILE_NOT_FOUND);
                                outputStream = null;

                                System.out.println(Messages.TRY_ONE_MORE_TIME);

                                if (Messages.YES.equals(new Scanner(System.in).nextLine())) {
                                    tryMore = true;
                                }
                            }
                        }
                        while (tryMore);
                    }


                    if (inputStream != null && outputStream != null) {
                        if (num == 1) {
                            handler.addTask(new Task(inputStream, outputStream,
                                    new DefaultConverter(), new ConverterXml()));
                        } else if (num == 2) {
                            handler.addTask(new Task(inputStream, outputStream,
                                    new ConverterXml(), new DefaultConverter()));
                        }

                        System.out.println(Messages.TASK_ADDED);
                    }

                }
            }
        }

        handler.killExecutor();

    }
}
