package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
public class PasswordUtils {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String ZYD_SECURITY_KEY = "929123f8f17944e8b0a531045453e1f1";

    public static String MD5(String s) {
        return DigestUtils.md5DigestAsHex(s.getBytes());
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
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        byte[] byteContent = password.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(md5Salt));
        byte[] result = cipher.doFinal(byteContent);
        return Base64.encodeBase64String(result);
    }


    public static String decrypt(String encryptPassword, String salt) throws Exception {
        String md5Salt = MD5(salt + ZYD_SECURITY_KEY);
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(md5Salt));
        byte[] result = cipher.doFinal(Base64.decodeBase64(encryptPassword));
        return new String(result);
    }
}
