package com.example.gmall.service;

import com.example.gmall.bean.PmsBaseCatalog1;
import com.example.gmall.bean.PmsBaseCatalog2;
import com.example.gmall.bean.PmsBaseCatalog3;

import java.util.List;

public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);
}
