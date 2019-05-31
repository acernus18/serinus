package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.utility.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class UserService {

    @Autowired
    private SerinusUserMapper userMapper;

    public String getCurrentPrincipal() {
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

    @Transactional
    public void updateUserLastLoginInfo(SerinusUser user) {
        if (user != null) {
            SerinusUser selective = new SerinusUser();

            selective.setId(user.getId());
            selective.setLoginCount(user.getLoginCount() + 1);
            selective.setLastLoginTime(new Date());
            selective.setLastLoginIp(RequestHelper.getRealIp());

            userMapper.updateByPrimaryKeySelective(user);
        }
    }
}
