package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.repository.SerinusStrategyMapper;
import org.maples.serinus.utility.SerinusHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class StrategyService {

    @Autowired
    private SerinusStrategyMapper strategyMapper;


    @Transactional
    public void saveStrategy(SerinusStrategy strategy) {
        if (StringUtils.isEmpty(strategy.getTitle()) || StringUtils.isEmpty(strategy.getProduct())) {
            return;
        }

        boolean formatValidation = false;
        JSONObject filter = null;
        JSONObject content = null;
        try {
            filter = JSON.parseObject(strategy.getFilter());
            content = JSON.parseObject(strategy.getContent());
            formatValidation = true;
        } catch (JSONException e) {
            log.info(e.getLocalizedMessage());
        }

        if (formatValidation && filter != null && content != null) {
            strategy.setFilter(Base64.encodeBase64String(filter.toJSONString().getBytes()));
            strategy.setFilter(Base64.encodeBase64String(content.toJSONString().getBytes()));

            if (strategy.getUuid() != null) {
                // Confirm that an object with same id existing in database;
                SerinusStrategy dbObject = strategyMapper.selectByPrimaryKey(strategy.getUuid());
                if (dbObject != null) {
                    // Confirm that two object has same title and product
                    boolean identity = dbObject.getProduct().equals(strategy.getProduct());
                    identity = identity && dbObject.getTitle().equals(strategy.getTitle());
                    if (identity) {
                        BeanUtils.copyProperties(strategy, dbObject);
                        strategyMapper.updateByPrimaryKey(dbObject);
                        return;
                    }
                }
            }

            strategy.setUuid(SerinusHelper.generateUUID());
            strategyMapper.insert(strategy);
        }
    }
}
