package org.maples.serinus.component;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.tomcat.util.codec.binary.Base64;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    private static final String SHIRO_LOGIN_COUNT = "shiro_login_count_";
    private static final String SHIRO_IS_LOCK = "shiro_is_lock_";
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    private String encrypt(String password, String content) throws Exception {
        // 创建密码器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);

        // 初始化为加密模式的密码器
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));

        // 加密
        byte[] result = cipher.doFinal(byteContent);

        //通过Base64转码返回
        return Base64.encodeBase64String(result);
    }

    private String decrypt(String password, String encrypted) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));

        //执行操作
        byte[] result = cipher.doFinal(Base64.decodeBase64(encrypted));

        return new String(result, StandardCharsets.UTF_8);
    }

    private SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        // javax.crypto.BadPaddingException: Given final block not properly padded解决方案
        // https://www.cnblogs.com/zempty/p/4318902.html - 用此法解决的
        // https://www.cnblogs.com/digdeep/p/5580244.html - 留作参考吧
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        //AES 要求密钥长度为 128
        kg.init(128, random);

        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        // 转换为AES专用密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        long userId = (long) info.getPrincipals().getPrimaryPrincipal();

        SerinusUser user = userMapper.selectByPrimaryKey(userId);
        String username = user.getPrincipal();

        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String loginCountKey = SHIRO_LOGIN_COUNT + username;
        String isLockKey = SHIRO_IS_LOCK + username;
        opsForValue.increment(loginCountKey, 1);

        Boolean locked = redisTemplate.hasKey(isLockKey);
        if (locked != null && locked) {
            throw new ExcessiveAttemptsException("Account [" + username + "] has been locked");
        }

        String loginCount = String.valueOf(opsForValue.get(loginCountKey));
        int retryCount = (5 - Integer.parseInt(loginCount));
        if (retryCount <= 0) {
            opsForValue.set(isLockKey, "LOCK", 1, TimeUnit.HOURS);
            redisTemplate.expire(loginCountKey, 1, TimeUnit.HOURS);
            throw new ExcessiveAttemptsException("Account [" + username + "] has been locked");
        }

        boolean matchResult;

        UsernamePasswordToken uToken = (UsernamePasswordToken) token;
        String inPassword = new String(uToken.getPassword());
        String dbPassword = (String) info.getCredentials();

        try {
            dbPassword = decrypt(dbPassword, uToken.getUsername());
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
            return false;
        }

        matchResult = this.equals(inPassword, dbPassword);

        if (!matchResult) {
            throw new AccountException("Invalid account or password, retry count left = " + retryCount);
        }

        redisTemplate.delete(loginCountKey);
        userService.updateUserLastLoginInfo(user);

        redisSessionDAO.addAttribute(JSON.toJSONString(user));
        return true;
    }
}
