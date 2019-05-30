package org.maples.serinus.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.maples.serinus.utility.ParseUtility;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_config")
public class SerinusConfig {

    @AllArgsConstructor
    public enum STATUS {
        ONLINE((short) 0), OFFLINE((short) 1);

        @Getter
        private short value;
    }

    @AllArgsConstructor
    public enum TYPE {
        INI((short) 0),
        JSON((short) 1),
        POLICY((short) 2),
        BD_CONFIGURATION((short) 3);

        @Getter
        private short value;
    }

    public void encode() {
        if (StringUtils.isNotBlank(cKey)) {
            cMd5 = DigestUtils.md5DigestAsHex(cKey.getBytes());
        }

        value = Base64Utils.encodeToString(value.getBytes());
        extra = Base64Utils.encodeToString(extra.getBytes());
        note = Base64Utils.encodeToString(note.getBytes());
        valueHistory = Base64Utils.encodeToString(valueHistory.getBytes());
    }

    public void decode() {
        value = new String(Base64Utils.decodeFromString(value));
        extra = new String(Base64Utils.decodeFromString(extra));
        note = new String(Base64Utils.decodeFromString(note));
        valueHistory = new String(Base64Utils.decodeFromString(valueHistory));

        jsonValue = parseJSONValue().toJSONString();
    }

    public JSON parseJSONValue() {
        if (StringUtils.isBlank(value)) {
            return new JSONObject();
        }

        if (type == TYPE.INI.getValue()) {
            return ParseUtility.parseINIString(value);
        } else {
            return ParseUtility.parseJSONString(value);
        }
    }

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "c_key")
    private String cKey;

    @Column(name = "c_md5")
    private String cMd5;

    @Column(name = "update_time")
    private Integer updateTime;

    @Column(name = "create_time")
    private Integer createTime;

    @Column(name = "op_uid")
    private Integer opUid;

    private String extra;

    private Byte type;

    private String note;

    private Byte status;

    private String value;

    @Column(name = "value_history")
    private String valueHistory;

    @Column(name = "json_value")
    private String jsonValue;
}