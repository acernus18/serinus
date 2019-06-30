package org.maples.serinus.controller.restful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.maples.serinus.model.SerinusConfig;
import org.maples.serinus.service.ConfigCacheService;
import org.maples.serinus.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.maples.serinus.utility.RequestHelper.createQueryString;

@CrossOrigin
@RestController
@RequestMapping("/conf-service")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigCacheService configCacheService;

    private Object createResult(Object value) {
        Map<String, Object> result = new HashMap<>();
        result.put("errno", 0);
        result.put("errmsg", "Success");
        result.put("data", value);

        return result;
    }

    @PostMapping("/save")
    public Object update(@RequestBody SerinusConfig configs) {
        return createResult(configService.save(configs));
    }

    @GetMapping("/get")
    public Object get(long id) {
        return createResult(configService.findById(id));
    }

    @GetMapping("/list")
    public Object list(@RequestParam(name = "product", required = false) String product,
                       @RequestParam(name = "fuzzy_search", required = false) String word,
                       @RequestParam(name = "status", required = false) String status,
                       @RequestParam(name = "page") String page,
                       @RequestParam(name = "page_length") String length,
                       @RequestParam(name = "sort_key") String sortKey,
                       @RequestParam(name = "sort_order") String sortOrder) {

        int pageValue = Integer.valueOf(page);
        int pageSizeValue = Integer.valueOf(length);

        Integer statusValue = null;
        if (StringUtils.isNotBlank(status)) {
            int value = Integer.valueOf(status);

            if (value == 0 || value == 1) {
                statusValue = value;
            }
        }

        if ("updateTime".equalsIgnoreCase(sortKey)) {
            sortKey = "update_time";
        } else if ("createTime".equalsIgnoreCase(sortKey)) {
            sortKey = "create_time";
        }

        boolean order = "asc".equalsIgnoreCase(sortOrder);
        PageInfo<SerinusConfig> result = configService.search(product, word, statusValue,
                pageValue, pageSizeValue, sortKey, order);

        return createResult(result);
    }

    @GetMapping("/dump")
    public Object dump(long id) throws IOException {
        return createResult(configService.exportConfigToFile(id));
    }

    @PostMapping("/rollback")
    public Object rollback(long id) {
        return createResult(configService.rollback(id));
    }

    @GetMapping("/query")
    public Object query(String key, @RequestParam(defaultValue = "0") Integer nocache) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("Key Invalid");
        }
        return createResult(configService.getJsonValueByKey(key, nocache == 0));
    }

    @RequestMapping(value = "/query/{key}", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject queryPolicy(@PathVariable String key, HttpServletRequest request) {
        String result = configService.getPolicyByKeyAndParams(key, createQueryString(request));
        return JSON.parseObject(result);
    }

    @RequestMapping(value = "/query/standard/{key}", method = {RequestMethod.GET, RequestMethod.POST})
    public Object queryPolicyWithStandardReturn(@PathVariable String key, HttpServletRequest request) {
        String result = configService.getPolicyByKeyAndParams(key, createQueryString(request));
        return createResult(JSON.parseObject(result));
    }

    @GetMapping("/fetchSlaveStatus")
    public Object fetchSlaveStatus() {
        return configCacheService.fetchSlaveStatus();
    }

    @GetMapping("/flush/all")
    public Object flushAll() {
        configCacheService.flushConcentrationCacheAsync();
        return createResult(true);
    }

    @GetMapping("/flush")
    public Object flushKey(@RequestParam String cKey) {
        return createResult(configCacheService.flushConcentrationCache(cKey));
    }
}

