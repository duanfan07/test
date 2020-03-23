package com.example.gmall.service;

import com.example.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {

    void checkCart(OmsCartItem omsCartItem);

    List<OmsCartItem> cartList(String memberId);

    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);
}
