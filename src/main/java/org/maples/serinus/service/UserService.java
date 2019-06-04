package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.utility.PasswordUtil;
import org.maples.serinus.utility.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private SerinusUserMapper userMapper;

    public List<SerinusUser> getSerinusUsers() {
        return userMapper.selectAll();
    }

    public String getCurrentPrincipal() {
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

    public void active(String principal) {
        SerinusUser user = userMapper.selectOneByPrincipal(principal);

        if (user != null) {
            SerinusUser selective = new SerinusUser();
            selective.setId(user.getId());
            selective.setStatus(1);
            userMapper.updateByPrimaryKeySelective(selective);
        }
    }

    @Transactional
    public void addSerinusUser(SerinusUser user) throws Exception {
        user.setStatus(0);
        user.setRegIp(RequestHelper.getRealIp());
        user.setCredential(PasswordUtil.encrypt(user.getCredential(), user.getPrincipal()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
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
