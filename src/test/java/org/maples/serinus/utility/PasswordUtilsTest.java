package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class PasswordUtilsTest {

    @Test
    public void test() throws Exception {
        //CGUx1FN++xS+4wNDFeN6DA==
        log.info(PasswordUtils.encrypt("123456", "root"));
    }
}