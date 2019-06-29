package org.maples.serinus.utility;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class CommonTest {

    @Test
    public void test() {
        // Using this env variable to create temp file;
        log.info(System.getProperty("java.io.tmpdir"));
    }
}
