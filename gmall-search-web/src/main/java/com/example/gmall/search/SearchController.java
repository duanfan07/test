package com.example.gmall.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.gmall.annotations.LoginRequired;
import com.example.gmall.bean.*;
import com.example.gmall.service.AttrSevice;
import com.example.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.*;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrSevice attrSevice;

    @RequestMapping("index")
    @LoginRequired(loginSuccess = false)
    public  String index(){
        return  "index";
    }

    @RequestMapping("list.html")
    public  String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){ //pmsSearchParam封装查询参数

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);

        modelMap.put("skuLsInfoList",pmsSearchSkuInfos);

        //抽取属性(采用set去重)
        Set<String> attrValueIdSet = new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String attrValueId = pmsSkuAttrValue.getValueId();
                attrValueIdSet.add(attrValueId);
            }
        }

        //根据valueId去查询属性值

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrSevice.getAttrValueListByValueId(attrValueIdSet);
        modelMap.put("attrList",pmsBaseAttrInfos);

//        //对平台属性进一步处理，去掉当前条件中valueId所在的属性组
//        String[] delValueIds = pmsSearchParam.getValueId();
//        if(delValueIds!=null){
//            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
//            while(iterator.hasNext()){
//                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
//                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
//                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
//                    String valueId = pmsBaseAttrValue.getId();
//                    for (String delValueId : delValueIds) {
//                        if(delValueId.equals(valueId)){
//                            //删除该属性值所在的属性组
//                            iterator.remove();
//                        }
//                    }
//                }
//            }
//        }


        // 对平台属性集合进一步处理，去掉当前条件中valueId所在的属性组
        String[] delValueIds = pmsSearchParam.getValueId();
        if (delValueIds != null) {
            // 面包屑
            // pmsSearchParam
            // delValueIds
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            for (String delValueId : delValueIds) {
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                // 生成面包屑的参数
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam, delValueId));
                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                        String valueId = pmsBaseAttrValue.getId();
                        if (delValueId.equals(valueId)) {
                            // 查找面包屑的属性值名称
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            //删除该属性值所在的属性组
                            iterator.remove();
                        }
                    }
                }
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList", pmsSearchCrumbs);
        }




        //url处理
        modelMap.put("urlParam",getUrlParam(pmsSearchParam));

        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            modelMap.put("keyword",keyword);
        }

//        // 面包屑
//        // pmsSearchParam
//        // delValueIds
//        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
//        if(delValueIds!=null){
//            // 如果delvalueIds参数不为空，说明当前请求中包含属性的参数，每一个属性参数，都会生成一个面包屑
//            for (String delValueId : delValueIds) {
//                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
//                // 生成面包屑的参数
//                pmsSearchCrumb.setValueId(delValueId);
//                pmsSearchCrumb.setValueName(delValueId);
//                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam,delValueId));
//                pmsSearchCrumbs.add(pmsSearchCrumb);
//            }
//        }
//        modelMap.put("attrValueSelectedList",pmsSearchCrumbs);



        return "list";
    }

    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam,String delValueId){
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        String  urlParam = "";
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if(skuAttrValueList!=null){
            for (String valueId : skuAttrValueList) {
                if (!valueId.equals(delValueId)) {
                    urlParam = urlParam + "&valueId=" + valueId;
                }

            }
        }

        return urlParam;
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam){
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        String  urlParam = "";
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if(skuAttrValueList!=null){
            for (String  valueId : skuAttrValueList) {
                urlParam = urlParam + "&valueId=" + valueId;
            }
        }

        return urlParam;
    }


}
