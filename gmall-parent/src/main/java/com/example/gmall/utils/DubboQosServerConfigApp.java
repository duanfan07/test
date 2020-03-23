package com.example.gmall.utils;

import com.alibaba.dubbo.qos.common.Constants;
import com.alibaba.dubbo.qos.server.Server;
import org.springframework.boot.SpringApplication;

public class DubboQosServerConfigApp {
    public static void main(String[] args) {
//        //配置dubbo.qos.port端口
//        System.setProperty(Constants.QOS_PORT,"33333");
//        //配置dubbo.qos.accept.foreign.ip是否关闭远程连接
//        System.setProperty(Constants.ACCEPT_FOREIGN_IP,"false");
//        SpringApplication.run(DubboQosServerConfigApp.class, args);

        //关闭QOS服务
        Server.getInstance().stop();

    }
}
