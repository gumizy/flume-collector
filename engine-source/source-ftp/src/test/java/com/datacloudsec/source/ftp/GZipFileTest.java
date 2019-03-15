package com.datacloudsec.source.ftp;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipFileTest {

    private static final int BUFFER_SIZE = 1024;

    /**
     * @param args
     */
    public static void main(String[] args) {

        File inputFile = new File("D:/test.txt");

        File outputFile = new File("D:/test.zip");

        writeGZip(inputFile, outputFile);

        readGZip(outputFile);
    }

    public static void readGZip(File file) {

        GZIPInputStream gzipInputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            gzipInputStream = new GZIPInputStream(new FileInputStream(file));
            try (BufferedReader in = new BufferedReader(new InputStreamReader(gzipInputStream, Charset.defaultCharset()))) {
                String line;

                while ((line = in.readLine()) != null) {
//                    line = new String(line.getBytes("UTF-8"),"UTF-8");//将读取出来的GBK格式的代码转换成UTF-8
                    System.out.println(line);
                }

            }
//            baos = new ByteArrayOutputStream();
//
//            byte[] buf = new byte[BUFFER_SIZE];
//            int len = 0;
//            while ((len = gzipInputStream.read(buf, 0, BUFFER_SIZE)) != -1) {
//                baos.write(buf, 0, len);
//            }
//
//            baos.toByteArray();

//            String result = baos.toString("UTF-8");

//            System.out.println("result=" + result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipInputStream != null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeGZip(File inputFile, File outputFile) {

        GZIPOutputStream gzipOutputStream = null;
        InputStream in = null;
        try {
            gzipOutputStream = new GZIPOutputStream(new FileOutputStream(outputFile));

            in = new FileInputStream(inputFile);

            byte[] buffer = new byte[BUFFER_SIZE];

            int len = 0;
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
                gzipOutputStream.write(buffer, 0, len);
            }
            gzipOutputStream.finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipOutputStream != null) {
                try {
                    gzipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
