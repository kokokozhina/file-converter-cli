package com.kokokozhina.cli;

import com.kokokozhina.converter.ConverterXml;
import com.kokokozhina.handler.Handler;
import com.kokokozhina.converter.DefaultConverter;
import com.kokokozhina.task.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Cli {

    private static String getPath(String msg, String readOrWrite) {
        boolean tryMore;

        do {
            System.out.println(msg);

            tryMore = false;

            String filePath = (new Scanner(System.in)).nextLine();
            File file = new File(filePath);
            if (file.isFile() && (readOrWrite.equals("Read") && file.canRead()
                    || readOrWrite.equals("Write") && file.canWrite())) {
                return filePath;
            } else {
                System.out.println(Messages.FILE_NOT_FOUND);
                System.out.println(Messages.TRY_ONE_MORE_TIME);

                if (Messages.YES.equals(new Scanner(System.in).nextLine())) {
                    tryMore = true;
                }
            }
        }
        while (tryMore);

        return null;
    }

    private static InputStream getConnectionFromUrl(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            return conn.getInputStream();

        } catch (MalformedURLException ex) {
            System.out.println(Messages.MALFORMED_URL);
        } catch (IOException ex) {
            System.out.println(Messages.IO_EXCEPTION);
        }

        return null;
    }

    private static OutputStream getOutputStreamForConvertation()  {
        System.out.println(Messages.OUTPUT_WAY);


        try {
            int way = (new Scanner(System.in)).nextInt();

            if (way <= 0 || way > Messages.CNT_OUTPUT_WAYS) {
                throw new InputMismatchException();
            }

            if (way == 1) {

                String path = getPath(Messages.OUTPUT_FILE_PATH, "Write");

                return path != null ? new FileOutputStream(path) : null;

            } else if (way == 2) {
                URL url = new URL("http://localhost:9080");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                return conn.getOutputStream();
            }

        } catch (InputMismatchException ex) {
            System.out.println(Messages.WRONG_INPUT);
        } catch (FileNotFoundException e) {
            System.out.println(Messages.FILE_NOT_FOUND);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static InputStream getInputStreamForConvertation() {
        System.out.println(Messages.INPUT_WAY);
        InputStream inputStream = null;
        String path;

        try {
            int way = (new Scanner(System.in)).nextInt();
            switch(way) {
                case 1:
                    path = getPath(Messages.INPUT_FILE_PATH, "Read");
                    if(path != null) {
                        inputStream = new FileInputStream(path);
                    }
                    break;

                case 2:
                    System.out.println(Messages.URL_PATH);
                    path = (new Scanner(System.in)).nextLine();
                    inputStream = getConnectionFromUrl(path);
                    break;

                default:
                    throw new InputMismatchException();
            }
        } catch (InputMismatchException ex) {
            System.out.println(Messages.WRONG_INPUT);
        } catch (FileNotFoundException e) {
            System.out.println(Messages.FILE_NOT_FOUND);
        }

        return inputStream;
    }

    public static void main(String[] args) {
        boolean exit = false;

        Handler handler = new Handler();

        while (!exit) {
            System.out.println(Messages.INTRO);

            int num = -1;

            try {
                num = (new Scanner(System.in)).nextInt();

                if (num < 0 || num > Messages.CNT_FORMATS) {
                    throw new InputMismatchException();
                }

            } catch (InputMismatchException ex) {
                System.out.println(Messages.WRONG_INPUT);
                continue;
            }


            if (num == 0) {
                break;
            }

            InputStream inputStream = getInputStreamForConvertation();

            OutputStream outputStream = null;
            if(inputStream != null) {
                outputStream = getOutputStreamForConvertation();
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


        handler.killExecutor();

    }
}
