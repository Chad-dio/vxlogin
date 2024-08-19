package org.chad.vxlogin.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import lombok.RequiredArgsConstructor;
import org.chad.vxlogin.common.UrlHolder;
import org.chad.vxlogin.domain.dto.WechatRequestDTO;
import org.chad.vxlogin.domain.entity.Account;
import org.chad.vxlogin.domain.entity.Result;
import org.chad.vxlogin.domain.po.User;
import org.chad.vxlogin.domain.vo.QRCodeVO;
import org.chad.vxlogin.domain.vo.ScanVO;
import org.chad.vxlogin.service.UserService;
import org.chad.vxlogin.utils.WechatUtil;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
    public Result<QRCodeVO> getQRCode(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String content = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                + account.getAppId()
                + "&redirect_uri=" + account.getDomain()+account.getRedirectUri()
                + "?uuid=" + uuid
                + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
        System.out.println(content);
        int width = 200;
        int height = 200;
        QrConfig config = new QrConfig(width,height);
        config.setImg(FileUtil.file("mylogo.jpg"));
        return Result.success(new QRCodeVO(QrCodeUtil.generateAsBase64(content,config,"jpg"), uuid),
                "获取成功");
    }

    @GetMapping("/wechatLogin")
    public Result<Void> wechatLogin(String code, String state, String uuid) {
        return userService.wechatLogin(code, state, uuid);
    }

    @GetMapping("/qrLoginCallBack/")
    public Result<ScanVO> qrLoginCallBack(@RequestParam String uuid){
        return userService.getUser(uuid);
    }
}
