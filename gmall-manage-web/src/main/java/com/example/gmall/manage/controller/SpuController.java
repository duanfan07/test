package com.example.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.bean.PmsProductImage;
import com.example.gmall.bean.PmsProductInfo;
import com.example.gmall.bean.PmsProductSaleAttr;
import com.example.gmall.manage.util.PmsUploadUtil;
import com.example.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo>  spuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String  spuList(@RequestBody PmsProductInfo  pmsProductInfo){
        String success = spuService.saveSpuInfo(pmsProductInfo);
        return success;
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String  fileUpload(@RequestParam("file") MultipartFile multipartFile){

        //将图片或音频上传到文件服务器并返回该地址给前端
        String url = PmsUploadUtil.uploadImage(multipartFile);
        return url;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr>  spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrs;
    }
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage>  spuImageList(String spuId){

        List<PmsProductImage> pmsProductImages = spuService.spuImageList(spuId);
        return pmsProductImages;
    }

}
