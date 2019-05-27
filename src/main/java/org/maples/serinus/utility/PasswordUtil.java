package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
public class PasswordUtil {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String ZYD_SECURITY_KEY = "929123f8f17944e8b0a531045453e1f1";

    public static String MD5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error("MD5生成失败", e);
            return null;
        }
    }

    public static String MD5(String param, String salt) {
        return MD5(param + salt);
    }

    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kg.init(128, random);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    public static String encrypt(String password, String salt) throws Exception {
        String md5Salt = MD5(salt + ZYD_SECURITY_KEY);

        if (md5Salt == null) {
            return "";
        }

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        byte[] byteContent = password.getBytes(StandardCharsets.UTF_8);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(md5Salt));
        byte[] result = cipher.doFinal(byteContent);

        return Base64.encodeBase64String(result);
    }


    public static String decrypt(String encryptPassword, String salt) throws Exception {
        String md5Salt = MD5(salt + ZYD_SECURITY_KEY);

        if (md5Salt == null) {
            return "";
        }

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(md5Salt));
        byte[] result = cipher.doFinal(Base64.decodeBase64(encryptPassword));

        return new String(result, StandardCharsets.UTF_8);
    }
}
