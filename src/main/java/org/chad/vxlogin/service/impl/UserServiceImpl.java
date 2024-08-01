package org.chad.vxlogin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.chad.vxlogin.domain.entity.Account;
import org.chad.vxlogin.domain.entity.Token;
import org.chad.vxlogin.domain.po.User;
import org.chad.vxlogin.domain.po.UserWechatRoute;
import org.chad.vxlogin.domain.po.WechatUser;
import org.chad.vxlogin.mapper.UserMapper;
import org.chad.vxlogin.mapper.UserWechatRouteMapper;
import org.chad.vxlogin.service.UserService;
import org.chad.vxlogin.utils.HttpUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserWechatRouteMapper userWechatRouteMapper;

    private final Account account;

    private User nowUser = new User();
    @Override
    @Transactional
    public String wechatLogin(String code, String state) {
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
                LambdaQueryWrapper<UserWechatRoute> wrapper = Wrappers.lambdaQuery(UserWechatRoute.class)
                        .eq(UserWechatRoute::getVxId, wechatUser.getOpenid());
                UserWechatRoute route = userWechatRouteMapper.selectOne(wrapper);
                if(BeanUtil.isNotEmpty(route)){
                    User user = baseMapper.selectById(route.getUserId());
                    nowUser.setUsername(user.getUsername());
                    nowUser.setPassword(user.getPassword());
                    return "登录成功";
                }
                User user = new User();
                user.setUsername(wechatUser.getNickname());
                user.setPassword("123456");
                save(user);
                nowUser.setUsername(user.getUsername());
                nowUser.setPassword(user.getPassword());
                route = new UserWechatRoute();
                route.setUserId(user.getId());
                route.setVxId(wechatUser.getOpenid());
                userWechatRouteMapper.insert(route);
            } catch (IOException e) {
                return "登录失败";
            }
        }
        return "登录成功";
    }

    @Override
    public User getUser() {
        return nowUser;
    }
}
