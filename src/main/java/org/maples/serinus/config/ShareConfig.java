package org.maples.serinus.config;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;

@Configuration
public class ShareConfig {

    @Bean
    public DefaultRedisScript<String> dispatchScript() throws IOException {
        DefaultRedisScript<String> dispatchScript = new DefaultRedisScript<>();
        URL dispatchScriptURL = ResourceUtils.getURL("classpath:!schema/dispatch.lua");
        dispatchScript.setScriptText(IOUtils.toString(dispatchScriptURL));
        return dispatchScript;
    }

}
