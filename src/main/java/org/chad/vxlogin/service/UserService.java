package org.chad.vxlogin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.vxlogin.domain.entity.Result;
import org.chad.vxlogin.domain.po.User;
import org.chad.vxlogin.domain.vo.CodeVO;
import org.chad.vxlogin.domain.vo.ScanVO;

public interface UserService extends IService<User> {
    Result<Void> wechatLogin(String code, String state, String uuid);

    Result<CodeVO> wechatLoginPhone(String code);
}
