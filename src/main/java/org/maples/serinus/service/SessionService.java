package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.MapCache;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SessionService extends CachingSessionDAO {
    private static final String SESSION_PREFIX = "SESSION_";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static String serializeSession(Session session) throws IOException {
        if (session == null) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(session);
        return Base64.encodeToString(bos.toByteArray());
    }

    private static Session deserializeSession(String session) throws IOException, ClassNotFoundException {
        if (StringUtils.isBlank(session)) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(session));
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Session) ois.readObject();
    }

    public SessionService() {
        super.setCacheManager(new AbstractCacheManager() {
            @Override
            protected Cache<Serializable, Session> createCache(String name) throws CacheException {
                return new MapCache<>(name, new ConcurrentHashMap<>());
            }
        });
    }

    @Override
    protected void doUpdate(Session session) {
        try {
            String sessionKey = SESSION_PREFIX + session.getId();
            String sessionValue = serializeSession(session);

            // TODO: Handle session timeout;

            redisTemplate.opsForValue().set(sessionKey, sessionValue, 10, TimeUnit.MINUTES);
        } catch (IOException e) {
            log.info(e.getLocalizedMessage());
            throw new RuntimeException("Serializing fail");
        }
    }

    @Override
    protected void doDelete(Session session) {
        redisTemplate.delete(SESSION_PREFIX + session.getId());
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        this.doUpdate(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session result;
        try {
            String sessionValue = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            result = deserializeSession(sessionValue);
        } catch (IOException | ClassNotFoundException e) {
            log.info(e.getLocalizedMessage());
            throw new RuntimeException("Deserialize fail");
        }
        return result;
    }
}
