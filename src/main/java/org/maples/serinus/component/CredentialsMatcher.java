package org.maples.serinus.component;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.service.UserService;
import org.maples.serinus.utility.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
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
    private SessionAccessor sessionAccessor;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String userPrincipal = (String) info.getPrincipals().getPrimaryPrincipal();

        SerinusUser user = userMapper.selectOneByPrincipal(userPrincipal);
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
            dbPassword = PasswordUtils.decrypt(dbPassword, uToken.getUsername());
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

        sessionAccessor.addAttribute(JSON.toJSONString(user));
        return true;
    }
}
