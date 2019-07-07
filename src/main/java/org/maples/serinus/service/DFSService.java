package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.maples.serinus.model.SerinusFileMeta;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Service
public class DFSService {
    private static final boolean ENABLE = false;

    private TrackerServer trackerServer;
    private StorageServer storageServer;

    @PostConstruct
    public void postConstruct() {
        if (ENABLE) {
            try {
                String configFile = ResourceUtils.getFile("classpath:properties/dfs_client.conf").getAbsolutePath();
                log.info("Load fast dfs config file from = {}", configFile);
                ClientGlobal.init(configFile);

                TrackerClient trackerClient = new TrackerClient();
                trackerServer = trackerClient.getConnection();
                storageServer = trackerClient.getStoreStorage(trackerServer);
            } catch (IOException | MyException e) {
                log.error(e.getLocalizedMessage());
            }
        }
    }

    public void upload(SerinusFileMeta file, byte[] fileContent) throws Exception {
        log.info("File Name: " + file.getFilename() + "File Length:" + fileContent.length);

        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("uploader", file.getUploader());

        long startTime = System.currentTimeMillis();
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        String[] uploadResults = storageClient.upload_file(fileContent, file.getExtension(), metaList);
        log.info("uploading file time used:" + (System.currentTimeMillis() - startTime) + " ms");

        if (uploadResults == null) {
            log.error("upload file fail, error code:" + storageClient.getErrorCode());
        } else {
            String groupName = uploadResults[0];
            String remoteFileName = uploadResults[1];

            file.setGroupName(groupName);
            file.setRemoteFilename(remoteFileName);

            log.info("upload file success, group {}, remoteFileName {}, ", groupName, remoteFileName);

            FileInfo fileInfo = storageClient.query_file_info(file.getGroupName(), file.getRemoteFilename());
            file.setCrc32(fileInfo.getCrc32());
            file.setCreateTimestamp(fileInfo.getCreateTimestamp());
            file.setFileSize(fileInfo.getFileSize());
            file.setSourceIpAddr(fileInfo.getSourceIpAddr());

            log.info("Uploading finished, file = {}", JSON.toJSONString(file, true));
        }
    }

    public void fetchFileInfo(SerinusFileMeta file) throws Exception {
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        FileInfo fileInfo = storageClient.query_file_info(file.getGroupName(), file.getRemoteFilename());

        file.setCrc32(fileInfo.getCrc32());
        file.setCreateTimestamp(fileInfo.getCreateTimestamp());
        file.setFileSize(fileInfo.getFileSize());
        file.setSourceIpAddr(fileInfo.getSourceIpAddr());
    }

    public byte[] download(SerinusFileMeta file) {
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        byte[] fileContent;
        try {
            fileContent = storageClient.download_file(file.getGroupName(), file.getRemoteFilename());
        } catch (Exception e) {
            fileContent = new byte[0];
        }

        return fileContent;
    }

    public void delete(SerinusFileMeta file) throws Exception {
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        storageClient.delete_file(file.getGroupName(), file.getRemoteFilename());
    }
}
