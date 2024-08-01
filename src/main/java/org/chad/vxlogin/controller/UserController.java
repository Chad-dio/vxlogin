package org.chad.vxlogin.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import lombok.RequiredArgsConstructor;
import org.chad.vxlogin.domain.dto.WechatRequestDTO;
import org.chad.vxlogin.domain.entity.Account;
import org.chad.vxlogin.domain.po.User;
import org.chad.vxlogin.service.UserService;
import org.chad.vxlogin.utils.WechatUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final Account account;

    private final UserService userService;

    @GetMapping("/wechatCoon")
    public String checkWeChatCoon(WechatRequestDTO requestDTO) throws NoSuchAlgorithmException {
        if(WechatUtil.checkWeChat(requestDTO)){
            return requestDTO.getEchostr();
        }
        return "error";
    }

    @GetMapping("/getQRCode")
    @CrossOrigin
    public String getQRCode(){
        String content = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                + account.getAppId()
                + "&redirect_uri=" + account.getDomain()+account.getRedirectUri()
                + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
        System.out.println(content);
        int width = 200;
        int height = 200;
        QrConfig config = new QrConfig(width,height);
        config.setImg(FileUtil.file("mylogo.jpg"));
        return QrCodeUtil.generateAsBase64(content,config,"jpg");
    }

    @GetMapping("/wechatLogin")
    public String wechatLogin(String code,String state) {
        return userService.wechatLogin(code, state);
    }

    @GetMapping("/getUser")
    public User getUser(){
        return userService.getUser();
    }
}
