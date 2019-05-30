package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MediaServiceTest {

    @Autowired
    private MediaService mediaService;

    @Test
    public void test() throws Exception {
        mediaService.test("D:\\maple\\Downloads\\TestMP4.mp4");
    }
}