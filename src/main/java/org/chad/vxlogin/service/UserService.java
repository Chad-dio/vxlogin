package org.chad.vxlogin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.vxlogin.domain.po.User;

public interface UserService extends IService<User> {
    String wechatLogin(String code, String state);

    User getUser();
}
