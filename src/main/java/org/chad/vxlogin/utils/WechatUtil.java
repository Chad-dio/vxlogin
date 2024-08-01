package org.chad.vxlogin.utils;

import org.chad.vxlogin.domain.dto.WechatRequestDTO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WechatUtil {

    private static String wechatToken = "vxtoken";

    public static boolean checkWeChat(WechatRequestDTO requestDTO) throws NoSuchAlgorithmException {
        //1.将token、timestamp、nonce三个参数进行字典序排序
        String[] array = {wechatToken, requestDTO.getTimestamp(), requestDTO.getNonce()};
        Arrays.sort(array);
        //2.将三个参数字符串拼接成一个字符串
        String str = String.join("", array);
        //3.进行 SHA-1 加密
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        byte[] digest = messageDigest.digest(str.getBytes());
        //4.将二进制字节数组digest逐个转为16进制数据，进而转换为字符串数据
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String s = Integer.toHexString(b & 0xff);
            //5.遇到长度==1的数据时，前头补0
            if (s.length() == 1) {
                hexString.append(0);
            }
            hexString.append(s);
        }
        //6.加密后的字符串可与signature对比，标识该请求来源于微信
        return requestDTO.getSignature().equals(hexString.toString());
    }
}
