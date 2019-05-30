package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.maples.serinus.model.SerinusFileMeta;
import org.maples.serinus.repository.SerinusFileMetaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@Service
public class FileService {

    @Autowired
    private DFSService dfsService;

    @Autowired
    private SerinusFileMetaMapper fileMetaMapper;

    @Autowired
    private UserService userService;

    public List<SerinusFileMeta> listAllFileMeta() {
        return fileMetaMapper.selectAll();
    }

    @Transactional
    public SerinusFileMeta saveFile(MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename();

        if (fileName == null) {
            return null;
        }
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        SerinusFileMeta file = new SerinusFileMeta();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            byte[] fileBuff = IOUtils.toByteArray(inputStream);

            file.setFilename(fileName);
            file.setExtension(ext);
            file.setUploader(userService.getCurrentPrincipal());
            file.setMd5("");

            dfsService.upload(file, fileBuff);
            fileMetaMapper.insert(file);
        } catch (Exception e) {
            log.error("Exception [{}] raised in uploading process", e.getLocalizedMessage());
            throw new RuntimeException("Rollback transaction for upload exception");
        }

        return file;
    }

    public void downloadFile(HttpServletResponse response, int fileID) {
        SerinusFileMeta serinusFileMeta = fileMetaMapper.selectByPrimaryKey(fileID);
        if (serinusFileMeta == null) {
            throw new RuntimeException("Cannot find file id = " + fileID);
        }

        byte[] file = dfsService.download(serinusFileMeta);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + serinusFileMeta.getFilename());

        try (OutputStream outputStream = response.getOutputStream()) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file);
            IOUtils.copy(inputStream, outputStream);

            inputStream.close();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Exception downloading file id = {}, because {}", fileID, e.getLocalizedMessage());
            throw new RuntimeException("Exception downloading file");
        }
    }
}
