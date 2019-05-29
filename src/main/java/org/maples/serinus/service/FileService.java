package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.maples.serinus.model.SerinusFileMeta;
import org.maples.serinus.repository.SerinusFileMetaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class FileService {

    @Autowired
    private DFSService dfsService;

    @Autowired
    private SerinusFileMetaMapper fileMetaMapper;

    @Transactional
    public void saveFile(MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename();

        if (fileName == null) {
            return;
        }

        SerinusFileMeta file = new SerinusFileMeta();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            byte[] fileBuff = IOUtils.toByteArray(inputStream);

            file.setFilename(fileName);
            file.setExtension(ext);

            dfsService.upload(file, fileBuff);

            fileMetaMapper.insert(file);
        } catch (Exception e) {
            log.error("Exception [{}] raised in uploading process", e.getLocalizedMessage());
            throw new RuntimeException("Rollback transaction for upload exception");
        }
    }
}
