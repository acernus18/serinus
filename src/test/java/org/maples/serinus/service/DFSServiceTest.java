package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusFileMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DFSServiceTest {

    @Autowired
    private DFSService dfsService;

    @Test
    public void upload() throws Exception {
        File file = ResourceUtils.getFile("D:\\maple\\Downloads\\TestMP4.mp4");

        byte[] content = IOUtils.toByteArray(new FileInputStream(file));

        SerinusFileMeta fastDFSFile = new SerinusFileMeta();

        fastDFSFile.setFilename("TestMP4");
        fastDFSFile.setExtension("mp4");
        fastDFSFile.setUploader("maples");
        fastDFSFile.setMd5("");

        dfsService.upload(fastDFSFile, content);
        log.info(JSON.toJSONString(fastDFSFile, true));
    }

    @Test
    public void download() throws Exception {
        SerinusFileMeta fastDFSFile = new SerinusFileMeta();

        fastDFSFile.setGroupName("group0");
        fastDFSFile.setRemoteFilename("M00/00/00/wKgBfVzuMmaACpsrAF0gDH_xONA245.mp4");
        byte[] contents = dfsService.download(fastDFSFile);

        FileUtils.writeByteArrayToFile(new File("D:\\maple\\Downloads\\downloadTest.mp4"), contents);
    }
}