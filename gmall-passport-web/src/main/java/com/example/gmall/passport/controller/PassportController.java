package com.example.gmall.passport.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.exampl.gmall.util.HttpclientUtil;
import com.example.gmall.bean.UmsMember;
import com.example.gmall.service.UserService;
import com.example.gmall.util.JwtUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    UserService userService;

    @RequestMapping("vlogin")
    public  String vlogin(String code, HttpServletRequest request){
        //code换取access_token
        String accessTokenUrl = "https://api.weibo.com/oauth2/access_token";//?client_id=892314055&client_secret=aa5ca09df89817e2956c807449e70f5f&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","892314055");
        paramMap.put("client_secret","aa5ca09df89817e2956c807449e70f5f");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code",code);// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(accessTokenUrl, paramMap);

        Map<String,Object> access_map = JSON.parseObject(access_token_json, Map.class);

        //access_token 换取userinfo
        String uid = (String) access_map.get("uid");
        String access_token = (String) access_map.get("access_token");
        String userInfoUrl = "https://api.weibo.com/2/users/show.json?access_token=" + access_token +"&uid=" + uid;
        String user_json = HttpclientUtil.doGet(userInfoUrl);
        Map<String,Object> user_map = JSON.parseObject(user_json, Map.class);


        //用户信息入库，usertype 为新浪用户
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType(2);
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid((long) user_map.get("id"));
        umsMember.setCity((String) user_map.get("location"));
        String gender = (String) user_map.get("gender");
         if (gender.equals("m")){
             umsMember.setGender(1);
         }else {
             umsMember.setGender(0);
         }
        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberCheck = userService.checkOauthUser(umsMember);
        if (umsMemberCheck==null){
            userService.addOauthUser(umsMember);
        }else {
            umsMember = umsMemberCheck;
        }


        //生成jwt的token，并且重定向到首页，携带token
        String token = "";
        String memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("memberId",memberId);
        userMap.put("nickname",nickname);


        String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();// 从request中获取ip
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("gmall", userMap, ip);

        // 将token存入redis一份
        userService.addUserToken(token,memberId);


        return "redirect:http://search.gmall.com:8083/index?token="+token;

    }


    @RequestMapping("index")
    public  String index(String ReturnUrl, ModelMap modelMap){

       modelMap.put( "ReturnUrl" ,ReturnUrl);
        return  "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public  String login(UmsMember umsMember, HttpServletRequest request){

        String token = "";

        //调用UserService验证用户名和密码
          UmsMember umsMemberLogin = userService.login(umsMember);

          if(umsMemberLogin!=null){
              //登陆成功
              //用jwt制作token
              String memberId = umsMemberLogin.getId();
              String nickname = umsMemberLogin.getNickname();

              Map<String,Object> userMap = new HashMap<>();
              userMap.put("memberId",memberId);
              userMap.put("nickname",nickname);
              String ip = request.getHeader("x-forward-for"); //通过niginx转发的客户端ip
              if(StringUtils.isBlank(ip)){
                  ip = request.getRemoteAddr();//从request中获取ip
                  if(StringUtils.isBlank(ip)){
                      ip = "127.0.0.1";
                  }
              }
              token = JwtUtil.encode("gmall",userMap,ip);//需要用算法加密

              //将token存入redis
              userService.addUserToken(token,memberId);
          }else{
              // 登录失败
              token = "fail";
          }


        return  token;
    }

    @RequestMapping("verify")
    @ResponseBody
    public  String verify(String token,String currentIp){

        //通过jwt效验token
        Map<String,String> map = new HashMap<>();

        Map<String, Object> decode = JwtUtil.decode(token, "gmall", currentIp);
        if(decode!=null){
            map.put("status","success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
        }else {
            map.put("status","fail");
        }


        return JSON.toJSONString(map);
    }

}
