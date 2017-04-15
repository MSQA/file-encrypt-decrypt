package org.sauceggplant.io;

import com.alibaba.fastjson.JSON;
import org.sauceggplant.crypto.Crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.UUID;

public class FileSplit {

    public static void split(String srcFile, String destPath, int blockSize, String dataAlgorithm, String summaryAlgorithm) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        FileInputStream fileInputStream = new FileInputStream(new File(srcFile));
        RandomAccessFile randomAccessFile = new RandomAccessFile(destPath + "security", "rw");
        byte[] buffer = new byte[blockSize];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            //File2ByteArray
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
            byteArrayOutputStream.write(buffer, 0, length);
            byteArrayOutputStream.close();
            //ByteArray2PartFile
            String fileName = UUID.randomUUID().toString().replace("-", "");
            String key = UUID.randomUUID().toString().replace("-", "");
            FileOutputStream fileOutputStream = new FileOutputStream(new File(destPath + fileName));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte[] data = Crypto.digestData(byteArrayOutputStream.toByteArray(), key.getBytes(), Cipher.ENCRYPT_MODE, dataAlgorithm);
            bufferedOutputStream.write(data);
            bufferedOutputStream.close();
            //Security&Summary
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("{");
            stringBuffer.append("\"fileName\":\"" + fileName + "\",");
            stringBuffer.append("\"key\":\"" + key + "\",");
            stringBuffer.append("\"summary\":\"" + Crypto.summaryData(data, summaryAlgorithm) + "\"");
            stringBuffer.append("}\n");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(stringBuffer.toString().getBytes());
        }
        randomAccessFile.close();
        fileInputStream.close();
    }

    public static void split(String srcFile, String destPath, int blockSize) {
        try {
            split(srcFile, destPath, blockSize, "DES", "MD5");
        } catch (Exception e) {
            System.out.println("####Split Error!");
        }
    }

    public static void merge(String srcPath, String fileName, String dataAlgorithm) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(srcPath + "/" + fileName));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(srcPath + "/security"))));
        String line;
        while ((line = br.readLine()) != null) {
            //EachPartFile2ByteArray
            HashMap hashMap = JSON.parseObject(line, HashMap.class);
            String key = String.valueOf(hashMap.get("key"));
            String partFileName = String.valueOf(hashMap.get("fileName"));
            FileInputStream fileInputStream = new FileInputStream(srcPath + partFileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            fileInputStream.close();
            byteArrayOutputStream.close();
            //MergeData2File
            bufferedOutputStream.write(Crypto.digestData(byteArrayOutputStream.toByteArray(), key.getBytes(), Cipher.DECRYPT_MODE, dataAlgorithm));
        }
        bufferedOutputStream.close();
    }

    public static void merge(String srcPath, String fileName) {
        try {
            merge(srcPath, fileName, "DES");
        } catch (Exception e) {
            System.out.println("####Merge Error!");
        }
    }
}