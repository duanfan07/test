package com.example.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.bean.PmsBaseAttrInfo;
import com.example.gmall.bean.PmsBaseAttrValue;
import com.example.gmall.bean.PmsBaseSaleAttr;
import com.example.gmall.service.AttrSevice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
//跨域请求注解
@CrossOrigin
public class AttrController {
    @Reference
    AttrSevice attrService;

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList (String catalog3Id){

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttr(catalog3Id);
        return pmsBaseAttrInfos;
    }

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String success = attrService.saveAttrInfo(pmsBaseAttrInfo);

        return success;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public  List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;

    }


    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public  List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = attrService.baseSaleAttrList();
        return pmsBaseSaleAttrs;

    }
}
