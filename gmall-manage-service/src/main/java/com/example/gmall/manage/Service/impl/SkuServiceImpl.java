package com.example.gmall.manage.Service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.example.gmall.bean.PmsSkuAttrValue;
import com.example.gmall.bean.PmsSkuImage;
import com.example.gmall.bean.PmsSkuInfo;
import com.example.gmall.bean.PmsSkuSaleAttrValue;
import com.example.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.example.gmall.manage.mapper.PmsSkuImageMapper;
import com.example.gmall.manage.mapper.PmsSkuInfoMapper;
import com.example.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.example.gmall.service.SkuService;
import com.example.gmall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


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
    @Autowired
    RedisUtil redisUtil;

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


    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo  skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //查图片
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);

        //查销售属性值
        PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();
        pmsSkuSaleAttrValue.setSkuId(skuId);
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
        skuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValues);

        return skuInfo;
    }


    @Override
    public PmsSkuInfo getSkuById(String skuId, String ipAddr)  {

        System.out.println(ipAddr +" | "+ Thread.currentThread().getName());
        PmsSkuInfo pmsSkuInfo =null;
        // 获得redis链接
        Jedis jedis = redisUtil.getJedis();
        try {
            //获得key和value
            String skuKey = "sku:" + skuId + ":info";
            String skuJson = jedis.get(skuKey);
            if(StringUtils.isNotBlank(skuJson)){
                //如果redis有此值，则返回
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            }else{
                //如果redis无此值，则查库
                //为防止缓存击穿，设置分布式锁

                String token = UUID.randomUUID().toString(); //生成token标明是自己的锁，删除锁的时候防止误删
                String Ok = jedis.set("sku:" + skuId + ":lock",token,"nx","px",10*1000); //不设过期时间容易死锁
                if (StringUtils.isNotBlank(Ok)&& "OK".equals(Ok)){
                    System.out.println("sku:" + skuId + ":lock 拿到");
                    //nx锁设置10s成功，查库
                    pmsSkuInfo = getSkuByIdFromDb(skuId);
                    //将里查到的写入redis
                    if(pmsSkuInfo!=null){
                        jedis.set("sku:" + skuId + ":info",JSON.toJSONString(pmsSkuInfo));
                        String lockToken = jedis.get("sku:" + skuId + ":lock");
                        //token相同再删锁
                        if(token.equals(lockToken)){
                            //lua脚本，在查询锁的同时删除
                            String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                            jedis.eval(script, Collections.singletonList("lock"),Collections.singletonList(token));

                            //jedis.del("sku:" + skuId + ":lock");
                        }

                        System.out.println("sku:" + skuId + ":lock del");
                    }else{
                        //如果数据库不存在，应防止缓存穿透
                        jedis.setex("sku:" + skuId + ":info",3*60,JSON.toJSONString("null"));
                    }

                }else {
                    //锁设置失败，sleep 10s 然后重新访问此方法(自旋)
                    Thread.sleep(10*1000);
                    return getSkuById(skuId, ipAddr);
                }


            }



        }catch (Exception e){
        }finally {
            jedis.close();
        }
        return pmsSkuInfo;
    }



    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(select);

        }

        return pmsSkuInfos;
    }
}
