package com.kokokozhina.cli;

public interface Messages {

    Integer CNT_FORMATS = 2;

    String INTRO =
            "Hello! This is simple file converter\n" +
            "Press 0 to exit\n" +
            "Press 1 to convert JSON -> XML\n" +
            "Press 2 to convert XML -> JSON";

    String INPUT_FILE_PATH =
            "Type path to input file";

    String OUTPUT_FILE_PATH =
            "Type path to output file";

    String FILE_NOT_FOUND =
            "File was not found!";

    String TRY_ONE_MORE_TIME =
            "Do you want to try one more time? [y/n]";

    String YES = "y";

    String TASK_ADDED =
            "Task was added successfully";

    String WRONG_INPUT =
            "Wrong input! ";

    String INPUT_WAY =
            "Choose input way: \n" +
            "Press 1 for file\n" +
            "Press 2 for URL";

    String URL_PATH =
            "Type URL path to file";

    String MALFORMED_URL =
            "Malformed url!";

    String IO_EXCEPTION =
            "This is very strange! Please try one more time";

    String OUTPUT_WAY =
            "Choose output way: \n" +
            "Press 1 for file\n" +
            "Press 2 for post to localhost";

    Integer CNT_OUTPUT_WAYS = 2;


}

