package com.datacloudsec.config.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AESKit
 *
 * @author gumizy 2017/4/17
 */
public class AESKit {

    private static final Logger logger = LoggerFactory.getLogger(AESKit.class);

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return byte[]
     */
    public static byte[] encToBytes(String content, String password) {
        try {
            final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes("utf-8"));
            keyGen.init(128, secureRandom);
            final SecretKey secretKey = keyGen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return cipher.doFinal(byteContent);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | UnsupportedEncodingException |
                IllegalBlockSizeException | BadPaddingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 加密成字符串
     *
     * @param content  content
     * @param password password
     * @return
     */
    public static String enc(String content, String password) {
        byte[] bytes = encToBytes(content, password);
        if (bytes == null) return null;
        return bytes2Str(bytes);
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return byte[]
     */
    public static byte[] decToBytes(byte[] content, String password) {
        try {
            final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes("utf-8"));
            keyGen.init(128, secureRandom);
            final SecretKey secretKey = keyGen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                UnsupportedEncodingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content  content
     * @param password password
     * @return String
     */
    public static String dec(String content, String password) {
        if (content == null) {
            return null;
        }
        byte[] bytes = str2Bytes(content);
        if (bytes == null) return null;
        byte[] result = decToBytes(bytes, password);
        if (result == null) return null;
        return new String(result);
    }

    /**
     * 加密后的字节转化为字符串
     *
     * @param bytes bytes
     * @return
     */
    private static String bytes2Str(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 字符串转化为字节
     *
     * @param str str
     * @return
     */
    private static byte[] str2Bytes(String str) {
        if (str.length() < 1)
            return null;
        byte[] result = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            int high = Integer.parseInt(str.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(str.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        final String source = "{\"key\": 123}";
        final String password = "123456";
        System.out.println("加密前: " + source);

        String encStr = enc(source, password);
        System.out.println("加密后: " + encStr);

        String decStr = dec(encStr, password);
        System.out.println("解密后: " + decStr);
    }

}
