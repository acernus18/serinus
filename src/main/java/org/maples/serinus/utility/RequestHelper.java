package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
public class RequestHelper {

    private static ServletRequestAttributes getServletRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    }

    public static HttpServletRequest getRequest() {
        Thread current = Thread.currentThread();
        log.debug("getRequest -- Thread id :{}, name : {}", current.getId(), current.getName());

        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        Thread current = Thread.currentThread();
        log.debug("getResponse -- Thread id :{}, name : {}", current.getId(), current.getName());

        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getResponse();
    }

    public static String getRealIp() {
        HttpServletRequest request = getRequest();

        if (request == null) {
            return "";
        }

        String ip = request.getHeader("x-forwarded-for");

        if (checkIp(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (checkIp(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (checkIp(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    private static boolean checkIp(String ip) {
        return !StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    public static String createQueryString(HttpServletRequest request) {

        StringBuilder queryString = new StringBuilder();

        Enumeration<String> names = request.getParameterNames();

        if (names.hasMoreElements()) {
            String name =  names.nextElement();
            queryString.append(String.format("%s=%s", name, request.getParameter(name)));
        }
        return queryString.toString();
    }
}
