package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ParseUtilsTest {

    @Test
    public void test() {

        String s = "a[1]=1";

        Pattern pattern = Pattern.compile("^(.+)\\[(.+)]\\s*=(.*)$");

        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            log.info(matcher.group(1));
            log.info(matcher.group(2));
            log.info(matcher.group(3));
        }
    }
}