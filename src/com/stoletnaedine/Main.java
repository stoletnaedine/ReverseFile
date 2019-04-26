package com.stoletnaedine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class Main {

    private static long SIZE_BYTES = 65_536; // буфер 64кб

    public static void main(String[] args) throws IOException {

        Scanner in = new Scanner(System.in);
        System.out.println("Please, enter input filename (required!) and optional extras: ");

        String inputParameters = null;
        try {
            inputParameters = in.nextLine();
            while (inputParameters.isEmpty()) {
                System.out.println("Please, enter through the space bar:");
                System.out.println("1. Filename or path (required) - file will overwrite");
                System.out.println("2. Output filename — optional");
                System.out.println("3. Reversal type (bit or byte) — optional");
                inputParameters = in.nextLine();
            }
        } catch (InputMismatchException e) {
            e.printStackTrace();
        }

        in.close();
        List<String> parametersList = Arrays.asList(inputParameters.split(" ")); // разделяем строку на параметры
        String inputFileName = parametersList.get(0).trim(); // имя исходного файла
        String outputFileName;
        String reversalType = "byte"; // byte or bit, default = "byte"

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
            reverseFile.overwriteFile(inputFile, reversalType); // переворот и перезапись побайтово
        }
        else {
            if (parametersList.size() == 3) {
                reversalType = parametersList.get(2).toLowerCase().trim(); // тип переворота
            }
            outputFileName = parametersList.get(1).trim(); // имя выходного файла
            File outputFile = new File(outputFileName);
            reverseFile.writeToFile(inputFile, outputFile, reversalType); // переворот и запись в новый файл
        }
        System.out.println(System.currentTimeMillis() - startTime + " ms");
    }

    private void writeToFile (File inputFile, File outputFile, String type) throws IOException { // запись в новый файл

        long BUFFER_SIZE;
        long FILE_LENGTH = inputFile.length();

        System.out.println("File length: " + FILE_LENGTH + " bytes");

        BUFFER_SIZE = (FILE_LENGTH > SIZE_BYTES) ? SIZE_BYTES : FILE_LENGTH; // если файл меньше 64кб, то без буфера

        RandomAccessFile input = new RandomAccessFile(inputFile, "r");
        RandomAccessFile output = new RandomAccessFile(outputFile, "rw");

        long CURSOR = FILE_LENGTH - BUFFER_SIZE;
        byte[] buffer = new byte[(int) BUFFER_SIZE];
        byte[] result;

        while (CURSOR >= 0) { // берем буфер с хвоста, переворачиваем и пишем в новый файл
            input.seek(CURSOR);
            input.read(buffer);
            if (type.equals("bit")) { // если тип БИТ
                BitSet bits = BitSet.valueOf(buffer);
                result = reverseBits(bits).toByteArray();
            }
            else { // если тип БАЙТ
                result = reverseBytes(buffer);
            }
            output.write(result);
            CURSOR = CURSOR - BUFFER_SIZE;
            if (CURSOR < 0) {
                buffer = new byte[(int) (CURSOR + BUFFER_SIZE)];
                CURSOR = 0;
                input.seek(CURSOR);
                input.read(buffer);
                if (type.equals("bit")) {  // если тип БИТ
                    BitSet bits = BitSet.valueOf(buffer);
                    result = reverseBits(bits).toByteArray();
                }
                else {  // если тип БАЙТ
                    result = reverseBytes(buffer);
                }
                output.write(result);
                System.out.println("Complete!");
                return;
            }
        }
        input.close();
        output.close();
    }

    private void overwriteFile (File inputFile, String type) throws IOException { // перезапись файла

        long BUFFER_SIZE;
        long FILE_LENGTH = inputFile.length();

        System.out.println("File length: " + FILE_LENGTH + " bytes");

        long ITER = FILE_LENGTH / SIZE_BYTES;

        BUFFER_SIZE = (FILE_LENGTH > SIZE_BYTES) ? SIZE_BYTES : FILE_LENGTH; // если файл меньше 64кб, то без буфера

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

            if (type.equals("bit")) {
                BitSet bitsTail = BitSet.valueOf(bufferTail);
                resultTail = reverseBits(bitsTail).toByteArray();
                BitSet bitsHead = BitSet.valueOf(bufferHead);
                resultHead = reverseBits(bitsHead).toByteArray();
            }
            else {
                resultTail = reverseBytes(bufferTail);
                resultHead = reverseBytes(bufferHead);
            }

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

        if (type.equals("bit")) {
            BitSet bitsCenter = BitSet.valueOf(bufferCenter);
            resultCenter = reverseBits(bitsCenter).toByteArray();
        }
        else {
            resultCenter = reverseBytes(bufferCenter);
        }

        input.write(resultCenter);
        input.close();

        System.out.println("Complete!");
    }

    static BitSet reverseBits(BitSet bits) { // переворот бит
        for (int i = 0, j = bits.size(); i < j; i++, j--) {
            boolean temp = bits.get(i);
            bits.set(i, bits.get(j));
            bits.set(j, temp);
        }
        return bits;
    }

    static byte[] reverseBytes(byte[] arr) { // переворот байт
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            byte temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }
}