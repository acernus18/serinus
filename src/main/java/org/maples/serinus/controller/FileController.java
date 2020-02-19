package org.maples.serinus.controller;

import org.maples.serinus.model.SerinusFileMeta;
import org.maples.serinus.service.FileService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResultBean<Object> singleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResultBean<>(200, "fail", "file is empty");
        }

        SerinusFileMeta result = fileService.saveFile(file);
        return new ResultBean<>(200, "success", result);
    }

    @GetMapping("/download")
    public void downloadFileByFileID(int fileID, HttpServletResponse response) {
        fileService.downloadFile(response, fileID);
    }

    @GetMapping("/list")
    public ResultBean<List<SerinusFileMeta>> listAllFileMeta() {
        return new ResultBean<>(200, "success", fileService.listAllFileMeta());
    }
}
