package org.maples.serinus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.maples.serinus.model.SerinusConfig;
import org.maples.serinus.service.ConfigCacheService;
import org.maples.serinus.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.maples.serinus.utility.RequestHelper.createQueryString;

@Controller
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

    @GetMapping("/0b5b00d51b08d3918fb22033b797a87a.png")
    public String configImage() {
        return "forward:/static/image/0b5b00d51b08d3918fb22033b797a87a.png";
    }

    @GetMapping("/index")
    public String configIndex() {
        return "config/index";
    }

    @PostMapping("/save")
    @ResponseBody
    public Object update(@RequestBody SerinusConfig configs) {
        return createResult(configService.save(configs));
    }

    @GetMapping("/get")
    @ResponseBody
    public Object get(long id) {
        return createResult(configService.findById(id));
    }

    @GetMapping("/list")
    @ResponseBody
    public Object list(@RequestParam(name = "product", required = false) String product,
                       @RequestParam(name = "fuzzy_search", required = false) String word,
                       @RequestParam(name = "status", required = false) String status,
                       @RequestParam(name = "page") String page,
                       @RequestParam(name = "page_length") String length,
                       @RequestParam(name = "sort_key") String sortKey,
                       @RequestParam(name = "sort_order") String sortOrder) {

        int pageValue = Integer.parseInt(page);
        int pageSizeValue = Integer.parseInt(length);

        Integer statusValue = null;
        if (StringUtils.isNotBlank(status)) {
            int value = Integer.parseInt(status);

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
    @ResponseBody
    public Object dump(long id) throws IOException {
        return createResult(configService.exportConfigToFile(id));
    }

    @PostMapping("/rollback")
    @ResponseBody
    public Object rollback(long id) {
        return createResult(configService.rollback(id));
    }

    @GetMapping("/query")
    @ResponseBody
    public Object query(String key, @RequestParam(defaultValue = "0") Integer nocache) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("Key Invalid");
        }
        return createResult(configService.getJsonValueByKey(key, nocache == 0));
    }

    @RequestMapping(value = "/query/{key}", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject queryPolicy(@PathVariable String key, HttpServletRequest request) {
        String result = configService.getPolicyByKeyAndParams(key, createQueryString(request));
        return JSON.parseObject(result);
    }

    @RequestMapping(value = "/query/standard/{key}", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object queryPolicyWithStandardReturn(@PathVariable String key, HttpServletRequest request) {
        String result = configService.getPolicyByKeyAndParams(key, createQueryString(request));
        return createResult(JSON.parseObject(result));
    }

    @GetMapping("/fetchSlaveStatus")
    @ResponseBody
    public Object fetchSlaveStatus() {
        return configCacheService.fetchSlaveStatus();
    }

    @GetMapping("/flush/all")
    @ResponseBody
    public Object flushAll() {
        configCacheService.flushConcentrationCacheAsync();
        return createResult(true);
    }

    @GetMapping("/flush")
    @ResponseBody
    public Object flushKey(@RequestParam String cKey) {
        return createResult(configCacheService.flushConcentrationCache(cKey));
    }
}

