package org.chad.vxlogin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.chad.vxlogin.domain.entity.Account;
import org.chad.vxlogin.domain.entity.Result;
import org.chad.vxlogin.domain.entity.Token;
import org.chad.vxlogin.domain.po.User;
import org.chad.vxlogin.domain.po.UserWechatRoute;
import org.chad.vxlogin.domain.po.WechatUser;
import org.chad.vxlogin.domain.vo.CodeVO;
import org.chad.vxlogin.domain.vo.ScanVO;
import org.chad.vxlogin.mapper.UserMapper;
import org.chad.vxlogin.mapper.UserWechatRouteMapper;
import org.chad.vxlogin.service.UserService;
import org.chad.vxlogin.utils.HttpUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    private final UserWechatRouteMapper userWechatRouteMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final Account account;
    @Override
    @Transactional
    public Result<Void> wechatLogin(String code, String state, String uuid) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + account.getAppId()
                + "&secret=" + account.getAppSecret()
                + "&code=" + code
                + "&grant_type=authorization_code";
        HttpResponse httpResponse = HttpUtil.doGet(url);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode==200){
            HttpEntity entity = httpResponse.getEntity();
            try {
                String entity_string = EntityUtils.toString(entity);
                Token token = JSON.parseObject(entity_string, Token.class);
                String access_token = token.getAccess_token();
                String open_id = token.getOpenid();
                url = "https://api.weixin.qq.com/sns/userinfo?access_token=" +
                        access_token + "&openid=" + open_id + "&lang=zh_CN";
                httpResponse = HttpUtil.doGet(url);
                entity = httpResponse.getEntity();
                entity_string = EntityUtils.toString(entity,"UTF-8");
                WechatUser wechatUser = JSON.parseObject(entity_string, WechatUser.class);
                System.out.println("当前扫码的微信用户是:\n" + wechatUser);
                stringRedisTemplate.opsForValue().set(uuid, wechatUser.getOpenid(), 30, TimeUnit.MINUTES);
            } catch (IOException e) {
                return Result.error("失败");
            }
        }else{
            return Result.success("登录失败");
        }
        return Result.error("失败");
    }

    @Override
    public Result<CodeVO> wechatLoginPhone(String code) {
        String openid = getOpenid(code);
        if(openid == null){
            return Result.error("登陆错误");
        }
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(uuid, openid, 30, TimeUnit.MINUTES);
        return Result.success(new CodeVO(uuid), "登陆成功");
    }

    private String getOpenid(String code){
        String url = WX_LOGIN + "?appid=" + account.getAppId()
                + "&secret=" + account.getAppSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        HttpResponse httpResponse = HttpUtil.doGet(url);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode == 200){
            HttpEntity entity = httpResponse.getEntity();
            try {
                String entity_string = EntityUtils.toString(entity);
                JSONObject jsonObject = JSON.parseObject(entity_string);
                String openid = jsonObject.getString("openid");
                return openid;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
