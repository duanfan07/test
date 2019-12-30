package com.example.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.bean.UmsMember;
import com.example.gmall.bean.UmsMemberReceiveAddress;
import com.example.gmall.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public  List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddress =  userService.getReceiveAddressByMemberId(memberId);
        return  umsMemberReceiveAddress;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public  List<UmsMember> getAllUser(){
       List<UmsMember> umsMembers =  userService.getAllUser();
       return  umsMembers;
    }

    @RequestMapping("index")
    @ResponseBody
    public  String index(){
       return  "hello user";
    }

}

