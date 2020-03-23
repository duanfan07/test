package com.example.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.exampl.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {
    public static String getCode(){

        //1.发请求clentid    892314055
        //授权回调地址  http://passport.gmall.com:8085/vlogin
        String s1 = "https://api.weibo.com/oauth2/authorize?client_id=892314055&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin";
        String weiboPage = HttpclientUtil.doGet(s1);

        System.out.println(weiboPage);
        // 用户填写微博账户密码


        //2.获得code f9105a7cee374f702f20f19f0e2bba20
        String s2 = "http://passport.gmall.com:8085/vlogin?code=f9105a7cee374f702f20f19f0e2bba20";

        return  null;
    }

    public static String getAccess_token(){

        // 3 换取access_token(必须post)
        // client_secret= aa5ca09df89817e2956c807449e70f5f
        String s3 = "https://api.weibo.com/oauth2/access_token";//?client_id=892314055&client_secret=aa5ca09df89817e2956c807449e70f5f&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","892314055");
        paramMap.put("client_secret","aa5ca09df89817e2956c807449e70f5f");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code","3d9706b3ccf3cb197b49f45eb9e56b85");// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

        Map<String,String> access_map = JSON.parseObject(access_token_json, Map.class);

        System.out.println(access_map.get("access_token"));
        //{"access_token":"2.00UB4WnF0LeD5y337792f314_BwUcC","remind_in":"157679999","expires_in":157679999,"uid":"5312419624","isRealName":"false"}
        return  access_map.get("access_token");
    }
    public static Map<String, String> getUser_info(){

        //4.用 access_token查用户信息

        String s4 = "https://api.weibo.com/2/users/show.json?access_token=2.00UB4WnF0LeD5y337792f314_BwUcC&uid=5312419624";
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json, Map.class);

        return  user_map;
    }

    public static void main(String[] args) {

        getCode();
        getAccess_token();
        getUser_info();


    }
}
