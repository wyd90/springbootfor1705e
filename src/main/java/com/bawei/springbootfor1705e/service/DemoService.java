package com.bawei.springbootfor1705e.service;

import com.bawei.springbootfor1705e.bean.ResBean;
import com.bawei.springbootfor1705e.bean.SaleBean;
import com.bawei.springbootfor1705e.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public List<ResBean> getResBeans() {
        return demoDao.getDatas();
    }

    public SaleBean getSaleDatas() {
        return demoDao.getSaleDatas();
    }
}
