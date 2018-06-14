package com.kokokozhina.cli;

import com.kokokozhina.converter.ConverterXml;
import com.kokokozhina.handler.Handler;
import com.kokokozhina.converter.DefaultConverter;
import com.kokokozhina.task.Task;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Cli {

    private static Boolean outputToLocalHost = false;

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
        String path;
        OutputStream outputStream = null;

        try {
            int way = (new Scanner(System.in)).nextInt();

            switch(way) {
                case 1:
                    path = getPath(Messages.OUTPUT_FILE_PATH, "Write");
                    outputToLocalHost = false;
                    outputStream = path != null ? new FileOutputStream(path) : null;
                    break;
                case 2:
                    outputToLocalHost = true;
                    break;
                default:
                    System.out.println(Messages.WRONG_INPUT);
            }

        } catch (InputMismatchException ex) {
            System.out.println(Messages.WRONG_INPUT);
        } catch (FileNotFoundException e) {
            System.out.println(Messages.FILE_NOT_FOUND);
        }

        return outputStream;

    }


    private static void sendPostRequest(ByteArrayOutputStream baos) throws IOException {
        StringEntity entity = new StringEntity(baos.toString(),
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://localhost:9080/");
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);
//        System.out.println(response.getStatusLine().getStatusCode());
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
                    System.out.println(Messages.WRONG_INPUT);
            }
        } catch (InputMismatchException ex) {
            System.out.println(Messages.WRONG_INPUT);
        } catch (FileNotFoundException e) {
            System.out.println(Messages.FILE_NOT_FOUND);
        }

        return inputStream;
    }

    public static void main(String[] args) throws IOException {

        Handler handler = new Handler();

        while (true) {
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
            if(inputStream == null) {
                continue;
            }


            OutputStream outputStream = getOutputStreamForConvertation();

            if(outputToLocalHost) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                if(num == 1) {
                    handler.addTask(new Task(inputStream, buffer,
                            new DefaultConverter(), new DefaultConverter()));
                } else if(num == 2) {
                    handler.addTask(new Task(inputStream, buffer,
                            new ConverterXml(), new DefaultConverter()));
                }
                sendPostRequest(buffer);
                System.out.println(Messages.TASK_ADDED);
            } else {
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
