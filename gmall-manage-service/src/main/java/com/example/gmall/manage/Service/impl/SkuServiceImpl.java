package com.example.gmall.manage.Service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.example.gmall.bean.PmsSkuAttrValue;
import com.example.gmall.bean.PmsSkuImage;
import com.example.gmall.bean.PmsSkuInfo;
import com.example.gmall.bean.PmsSkuSaleAttrValue;
import com.example.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.example.gmall.manage.mapper.PmsSkuImageMapper;
import com.example.gmall.manage.mapper.PmsSkuInfoMapper;
import com.example.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.example.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SkuServiceImpl implements SkuService {


    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Transactional
    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

        String skuInfoId = pmsSkuInfo.getId();

        List<PmsSkuImage> pmsSkuImages = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : pmsSkuImages) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValues) {
            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValues) {
            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        return "success";
    }
}
