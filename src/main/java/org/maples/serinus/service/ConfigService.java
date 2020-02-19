package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.maples.serinus.model.SerinusConfig;
import org.maples.serinus.repository.SerinusConfigMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@CacheConfig(cacheNames = "CKey::Value::Cache")
public class ConfigService {

    @Autowired
    private SerinusConfigMapper mapper;

    private int getDateStamp() {
        return (int) (new Date().getTime() / 1000);
    }

    private boolean insert(SerinusConfig commonConfigs) {
        commonConfigs.setId(null);
        commonConfigs.setCreateTime(getDateStamp());
        commonConfigs.setValueHistory(commonConfigs.getValue());

        log.info("Insert new Config: \n{}", JSON.toJSONString(commonConfigs, true));
        return mapper.insertSelective(commonConfigs) == 1;
    }

    private boolean update(SerinusConfig source, SerinusConfig databaseObject) {
        if (!databaseObject.getId().equals(source.getId())) {
            throw new RuntimeException("Duplicate CKey");
        }
        String valueHistory = databaseObject.getValue();

        log.info("Modify Config Origin: \n{}", JSON.toJSONString(databaseObject, true));

        BeanUtils.copyProperties(source, databaseObject, "id");

        if (valueHistory.equals(databaseObject.getValue())) {
            databaseObject.setValue(null);
            databaseObject.setJsonValue(null);
            databaseObject.setValueHistory(null);
        } else {
            databaseObject.setValueHistory(valueHistory);
        }

        log.info("To: \n{}", JSON.toJSONString(databaseObject, true));
        return mapper.updateByPrimaryKeySelective(databaseObject) == 1;
    }

    private boolean predicate(Map<String, String> params, JSONObject conditions) {

        if (conditions.isEmpty()) {
            return true;
        }

        if (conditions.size() != params.size()) {
            return false;
        }

        for (String conditionKey : conditions.keySet()) {
            String[] key = conditionKey.split("\\.");
            if (!conditions.getString(conditionKey).equals(params.get(key[key.length - 1]))) {
                return false;
            }
        }

        return true;
    }

    private JSONObject compatibleWithJSONArray(Object paramFilter) {
        if (paramFilter instanceof JSONObject) {
            return (JSONObject) paramFilter;
        }

        if (paramFilter instanceof JSONArray) {
            if (!((JSONArray) paramFilter).isEmpty()) {
                throw new RuntimeException("req_param_filter is array and size != 0");
            }
            return new JSONObject();
        }

        throw new RuntimeException("req_param_filter`s type is invalid");
    }

    @Transactional
    public boolean save(SerinusConfig configs) {
        if (configs == null || configs.getType() == null || configs.getValue() == null) {
            return false;
        }

        // Verify format (JSON, INI, Policy Chain);
        if (configs.getType() < SerinusConfig.TYPE.POLICY.getValue()) {
            configs.setJsonValue(configs.parseJSONValue().toJSONString());
        } else {
            configs.setJsonValue("");
        }

        configs.encode();
        configs.setValueHistory(null);
        configs.setUpdateTime(getDateStamp());

        SerinusConfig databaseObject = mapper.selectOneByCKey(configs.getCKey());

        if (databaseObject != null) {
            return update(configs, databaseObject);
        } else {
            return insert(configs);
        }
    }

    public PageInfo<SerinusConfig> search(String product, String word, Integer status,
                                          int page, int pageSize,
                                          String orderKey, boolean order) {
        Page<SerinusConfig> pageInfo = PageHelper.startPage(page, pageSize);

        PageInfo<SerinusConfig> result = pageInfo.doSelectPageInfo(() ->
                mapper.selectByKeywordWithOrder(product, word, status, orderKey, order));

        result.getList().forEach(SerinusConfig::decode);
        return result;
    }


    public SerinusConfig findById(long id) {
        SerinusConfig serinusConfig = mapper.selectByPrimaryKey(id);
        serinusConfig.decode();
        return serinusConfig;
    }

    @Transactional
    public boolean rollback(long id) {
        // apiService.evictAllCache();
        return mapper.updateValueWithHistoryByID(id) == 1;
    }

    @CacheEvict(allEntries = true)
    public void evictAllCache() {
        log.debug("evict all cache");
    }

    @Cacheable(key = "#key", condition = "#cache")
    public JSON getJsonValueByKey(String key, boolean cache) {
        log.debug("key: {}, cache={}", key, cache);
        SerinusConfig configs = mapper.selectOneNormalByCKey(key);

        if (configs != null && configs.getValue() != null) {
            configs.decode();
            return configs.parseJSONValue();
        }
        return null;
    }

    @Cacheable
    public String getPolicyByKeyAndParams(String key, String query) {

        Map<String, String> paramsMap = new HashMap<>();

        if (StringUtils.isNotBlank(query)) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyAndValue = param.split("=");
                paramsMap.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        SerinusConfig configs = mapper.selectOnePolicyByCKey(key);

        if (configs != null && configs.getType() == SerinusConfig.TYPE.POLICY.getValue()) {
            configs.decode();
            JSONObject policyChain = JSON.parseObject(configs.getValue());

            JSONArray filters = policyChain.getJSONArray("policy_chain");

            for (int i = 0; i < filters.size(); i++) {
                JSONObject filter = filters.getJSONObject(i);

                JSONObject paramFilter = compatibleWithJSONArray(filter.get("req_param_filter"));
                if (predicate(paramsMap, paramFilter)) {
                    return filter.getString("content");
                }
            }
        }

        return "";
    }

    public String exportConfigToFile(long id) throws IOException {
        SerinusConfig configs = mapper.selectByPrimaryKey(id);
        if (configs == null || configs.getType() != 3) {
            return null;
        }

        configs.decode();

        String path = configs.getCKey().replace('.', '/');
        File dumpFile = new File("dump/" + path + "/" + configs.getCMd5() + ".conf");

        if (!dumpFile.getParentFile().exists()) {
            log.info("Make Dir {}", dumpFile.getParentFile().mkdirs());
        }

        IOUtils.write(configs.getValue(), new FileOutputStream(dumpFile));
        return dumpFile.getAbsolutePath();
    }
}
