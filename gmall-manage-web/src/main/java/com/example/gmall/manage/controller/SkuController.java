package com.example.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.bean.PmsSkuInfo;
import com.example.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
public class SkuController {
    @Reference
    SkuService skuService;


    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public  String  saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){

        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        // 处理默认图片
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if(StringUtils.isBlank(skuDefaultImg)){
            if(pmsSkuInfo.getSkuImageList().size()!=0) {
                pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
            }
        }

        String success = skuService.saveSkuInfo(pmsSkuInfo);
        return  success;

    }
}
