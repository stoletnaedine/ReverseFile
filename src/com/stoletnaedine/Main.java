package com.stoletnaedine;

import java.io.*;
import java.util.*;

public class Main {
    static long SIZE_BYTES = 33_554_432; // буффер 32 Мб
    static String inputFileName = null;
    static String outputFileName = null;

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Please, enter input filename (required!) and optional extras: ");
        String inputParameters = null;
        try {
            inputParameters = in.nextLine();
            while (inputParameters.isEmpty()) {
                printHelp();
                inputParameters = in.nextLine();
            }
        } catch (InputMismatchException e) {
            e.printStackTrace();
        }
        List<String> parametersList = Arrays.asList(inputParameters.split(" ")); // разделяем строку на параметры
        String inputFileName = parametersList.get(0).trim(); // имя исходного файла
        boolean isBitwise = false; // byte or bit, default = "byte"
        File inputFile = null;
        try {
            inputFile = new File(inputFileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (!inputFile.exists()) {
            throw new FileNotFoundException("File not found!");
        }
        long startTime = System.currentTimeMillis();
        Main reverseFile = new Main();
        if (parametersList.size() == 1) {
            reverseFile.overwriteFile(inputFile, isBitwise); // переворот и перезапись побайтово
        }
        else {
            if (parametersList.size() == 3 && parametersList.get(2).equals("bit")) {
                isBitwise = true; // тип переворота БИТ
            }
            outputFileName = parametersList.get(1).trim(); // имя выходного файла
            File outputFile = new File(outputFileName);
            reverseFile.writeToFile(inputFile, outputFile, isBitwise); // переворот и запись в новый файл
        }
        System.out.println(System.currentTimeMillis() - startTime + " ms");
    }

    void writeToFile (File inputFile, File outputFile, boolean isBitwise) throws IOException { // запись в новый файл
        long BUFFER_SIZE;
        long FILE_LENGTH = inputFile.length();
        BUFFER_SIZE = (FILE_LENGTH > SIZE_BYTES) ? SIZE_BYTES : FILE_LENGTH; // если файл меньше 32 Мб, то без буффера
        RandomAccessFile input = new RandomAccessFile(inputFile, "r");
        FileOutputStream output = new FileOutputStream(outputFile.getPath());
        long CURSOR = FILE_LENGTH - BUFFER_SIZE;
        byte[] buffer = new byte[(int) BUFFER_SIZE];
        byte[] result;
        while (CURSOR >= 0) { // берем буффер с хвоста, переворачиваем и пишем в новый файл
            input.seek(CURSOR);
            input.read(buffer);
            result = reverseBytes(buffer, isBitwise);
            output.write(result);
            CURSOR = CURSOR - BUFFER_SIZE;
            if (CURSOR < 0) {
                buffer = new byte[(int) (CURSOR + BUFFER_SIZE)];
                CURSOR = 0;
                input.seek(CURSOR);
                input.read(buffer);
                result = reverseBytes(buffer, isBitwise);
                output.write(result);
                System.out.printf("Файл '%s' перевернут и записан в файл '%s'.\n", inputFile.getName(), outputFile.getName());
                return;
            }
        }
    }

    void overwriteFile (File inputFile, boolean isBitwise) throws IOException { // перезапись файла
        long BUFFER_SIZE;
        long FILE_LENGTH = inputFile.length();
        long ITER = FILE_LENGTH / SIZE_BYTES;
        BUFFER_SIZE = (FILE_LENGTH > SIZE_BYTES) ? SIZE_BYTES : FILE_LENGTH; // если файл меньше 32 Мб, то без буффера
        RandomAccessFile input = new RandomAccessFile(inputFile, "rw");
        long CURSOR_TAIL = FILE_LENGTH - BUFFER_SIZE;
        long CURSOR_HEAD = 0;
        for (int i = 0; i < ITER / 2; i++) { // берем буфером хвост и голову файла, переворачиваем и меняем местами
            byte[] bufferHead = new byte[(int) BUFFER_SIZE];
            byte[] bufferTail = new byte[(int) BUFFER_SIZE];
            byte[] resultHead;
            byte[] resultTail;
            input.seek(CURSOR_TAIL);
            input.read(bufferTail);
            input.seek(CURSOR_HEAD);
            input.read(bufferHead);
            resultTail = reverseBytes(bufferTail, isBitwise);
            resultHead = reverseBytes(bufferHead, isBitwise);
            input.seek(CURSOR_HEAD);
            input.write(resultTail);
            input.seek(CURSOR_TAIL);
            input.write(resultHead);
            CURSOR_HEAD += BUFFER_SIZE;
            CURSOR_TAIL -= BUFFER_SIZE;
        }
        byte[] bufferCenter = new byte[(int) (CURSOR_TAIL - CURSOR_HEAD + BUFFER_SIZE)];
        byte[] resultCenter;
        input.seek(CURSOR_HEAD);
        input.read(bufferCenter);
        input.seek(CURSOR_HEAD);
        resultCenter = reverseBytes(bufferCenter, isBitwise);
        input.write(resultCenter);
        input.close();
        System.out.printf("Файл '%s' перевернут.\n", inputFile.getName());
    }

    static void printHelp() {
        System.out.println("Please, enter through the space bar:");
        System.out.println("1. Filename or path (required) - file will overwrite");
        System.out.println("2. Output filename — optional");
        System.out.println("3. Reversal type (bit or byte) — optional");
    }
    
    static byte reverseBits(byte value) {
        return (byte) (Integer.reverse(value & 0xFF) >>> 24);
	}

    static byte[] reverseBytes(byte[] bytes, boolean isBitwise) { // переворот байт/бит
        for (int i = 0, j = bytes.length - 1; i < j; i++, j--) {
            byte temp = (isBitwise) ? reverseBits(bytes[i]) : bytes[i];
            bytes[i] = (isBitwise) ? reverseBits(bytes[j]) : bytes[j];
            bytes[j] = temp;
        }
        return bytes;
    }
}