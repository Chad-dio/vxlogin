package org.chad.vxlogin.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatUser {
    private String openid;
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;
    private String privilege;
    private String unionid;

    public String toString(){
        return "用户名:" + nickname
                + "\n性别:" + sex
                + "\n省份:" + province
                + "\n城市:" + city
                + "\n微信号:" + openid + "\n";
    }
}