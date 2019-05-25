package org.maples.serinus.service;

import com.alibaba.fastjson.JSON;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.model.StrategyHistory;
import org.maples.serinus.repository.SerinusStrategyMapper;
import org.maples.serinus.repository.StrategyHistoryMapper;
import org.maples.serinus.utility.SerinusHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HistoryService {

    @Autowired
    private SerinusStrategyMapper strategyMapper;

    @Autowired
    private StrategyHistoryMapper historyMapper;

    public void updateStrategy(SerinusStrategy valueAfter) {

        String uuid = valueAfter.getUuid();

        SerinusStrategy valueBefore = strategyMapper.selectByPrimaryKey(uuid);

        if (valueBefore == null) {
            throw new RuntimeException("");
        }

        StrategyHistory history = new StrategyHistory();

        history.setUuid(SerinusHelper.generateUUID());
        history.setInspectorId(0);
        history.setOperatorId(0);
        history.setFlag(0);
        history.setTime(new Date());
        history.setProduct(valueAfter.getProduct());
        history.setStrategyUuid(uuid);
        history.setValueBefore(JSON.toJSONString(valueBefore));
        history.setValueAfter(JSON.toJSONString(valueAfter));
    }

    public void createStrategy(SerinusStrategy value) {
        StrategyHistory history = new StrategyHistory();
        history.setUuid(SerinusHelper.generateUUID());
        history.setInspectorId(1);
        history.setOperatorId(1);
        history.setFlag(0);
        history.setTime(new Date());
        history.setProduct(value.getProduct());
        history.setStrategyUuid(value.getUuid());
        history.setValueBefore("");
        history.setValueAfter(JSON.toJSONString(value));
    }

    public void rollback(String strategyUUID, String historyUUID) {

    }

    public void rollback(String strategyUUID, Date time) {

    }

    public void rollbackProduct(String product, Date time) {

    }
}
