package org.maples.serinus.utility;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResultBean<T> {

    private int status;
    private String message;
    private T data;

    public ResultBean(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String toJSON() {
        return JSON.toJSONString(this);
    }
}
