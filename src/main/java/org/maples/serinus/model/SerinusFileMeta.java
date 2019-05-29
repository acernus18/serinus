package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_file_meta")
public class SerinusFileMeta {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String filename;

    private String extension;

    private String md5;

    private String uploader;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "remote_filename")
    private String remoteFilename;

    @Column(name = "source_ip_addr")
    private String sourceIpAddr;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "create_timestamp")
    private Date createTimestamp;

    private Long crc32;
}