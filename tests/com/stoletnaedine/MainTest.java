package com.stoletnaedine;

import com.sun.tools.javac.util.Assert;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @org.junit.jupiter.api.Test
    void writeToFileTestLength() throws IOException {
        Main.inputFileName = "/Users/arturisart/IdeaProjects/HW1/tests/file";
        Main.outputFileName = "/Users/arturisart/IdeaProjects/HW1/tests/file2";
        File input = new File(Main.inputFileName);
        File output = new File(Main.outputFileName);
        Main reverse = new Main();
        reverse.writeToFile(input, output, false);
        assertEquals(input.length(), output.length());
    }

    @org.junit.jupiter.api.Test
    void writeToFileTestDoubleReverse() throws IOException {
        Main.inputFileName = "/Users/arturisart/IdeaProjects/HW1/tests/file";
        Main.outputFileName = "/Users/arturisart/IdeaProjects/HW1/tests/file2";
        String outputDoubleReverseFileName = "/Users/arturisart/IdeaProjects/HW1/tests/file3";
        File input = new File(Main.inputFileName);
        File output = new File(Main.outputFileName);
        File outputDoubleReverse = new File(outputDoubleReverseFileName);
        Main reverse = new Main();
        reverse.writeToFile(input, output, false);
        reverse.writeToFile(output, outputDoubleReverse, false);
        byte[] inputArr = new byte[(int) (input.length())];
        new RandomAccessFile(input, "r").read(inputArr);
        byte[] outputArr = new byte[(int) outputDoubleReverse.length()];
        new FileInputStream(outputDoubleReverseFileName).read(outputArr);
        assertArrayEquals(inputArr, outputArr);
    }

    @org.junit.jupiter.api.Test
    void overwriteFileTestLength() throws IOException {
        Main.inputFileName = "/Users/arturisart/IdeaProjects/HW1/tests/file";
        File input = new File(Main.inputFileName);
        long inputLength = input.length();
        Main reverse = new Main();
        reverse.overwriteFile(input, false);
        long outputLength = input.length();
        assertEquals(inputLength, outputLength);
    }

    @org.junit.jupiter.api.Test
    void reverseBytes() {
    }
}