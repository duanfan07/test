package com.example.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.example.gmall.bean.PmsProductSaleAttr;
import com.example.gmall.bean.PmsSkuInfo;
import com.example.gmall.bean.PmsSkuSaleAttrValue;
import com.example.gmall.service.SkuService;
import com.example.gmall.service.SpuService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String  skuId , ModelMap modelMap, HttpServletRequest request) throws Exception {

        String ipAddr = request.getRemoteAddr();

        //request.getHeader("");  //nginx负载均衡

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,ipAddr);
        //sku对象
        modelMap.put("skuInfo",pmsSkuInfo);

        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),skuId);
        //销售属性列表
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        //把skuid和对应的销售属性存入一个hashmap里面
        Map<String,String> skuSaleAttrHashMap = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";                //map的k
            String v = skuInfo.getId();  //map的v

            List<PmsSkuSaleAttrValue> skuSaleAttrValues = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
                k += skuSaleAttrValue.getSaleAttrValueId() + "|";  //249|250
            }

            skuSaleAttrHashMap.put(k,v);
        }

        //将skuSaleAttrHashMap放到页面上
        String skuSaleAttrHashMapString = JSON.toJSONString(skuSaleAttrHashMap);
        modelMap.put("skuSaleAttrHashJsonStr",skuSaleAttrHashMapString);


        return "item";
    }

    @RequestMapping("index")
    public String index(ModelMap modelMap){

        List<String> list = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            list.add("循环数据"+i);
        }

        modelMap.put("list",list);
        modelMap.put("hello","hello thymeleaf !!");

        modelMap.put("check",'0');


        return "index";
    }
}
