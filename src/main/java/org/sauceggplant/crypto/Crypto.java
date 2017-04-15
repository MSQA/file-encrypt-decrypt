package org.sauceggplant.crypto;

import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Crypto {

    public static byte[] digestData(byte[] data, byte[] key, int MODE, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        SecureRandom secureRandom = new SecureRandom();
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey securekey = keyFactory.generateSecret(new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(MODE, securekey, secureRandom);
        return cipher.doFinal(data);
    }

    public static String summaryFile(String file, String algorithm) throws IOException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int length;
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        while ((length = fileInputStream.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, length);
        }
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest());
    }

    public static String summaryData(byte[] data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(data);
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest());
    }
}