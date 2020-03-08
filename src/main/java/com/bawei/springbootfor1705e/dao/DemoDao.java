package com.bawei.springbootfor1705e.dao;

import com.bawei.springbootfor1705e.bean.ResBean;
import com.bawei.springbootfor1705e.bean.SaleBean;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DemoDao {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HbaseTemplate hbaseTemplate;

    public List<ResBean> getDatas(){
        Set<String> keys = redisTemplate.opsForHash().keys("access");
        List<ResBean> resBeans = new ArrayList<>();
        for(String key : keys) {
            Object accessNum = redisTemplate.opsForHash().get("access", key);
            ResBean resBean = new ResBean();
            resBean.setName(key);
            resBean.setValue(((Integer) accessNum).longValue());
            resBeans.add(resBean);
        }
        return resBeans;
    }


    public SaleBean getSaleDatas() {
        List<Map<String, Object>> maps = hbaseTemplate.find("ns5:t_click", new Scan(), new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(Result result, int rowNum) throws Exception {
                Map<String, Object> map = new HashMap<>();
                byte[] zoneByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("zone"));
                byte[] clickByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("click"));
                map.put("zone",Bytes.toString(zoneByte));
                map.put("click",Bytes.toInt(clickByte));
                return map;
            }
        });
        List<String> zones = new ArrayList<>();
        List<Integer> clicks = new ArrayList<>();
        for(Map<String,Object> map : maps){
            Object zoneObj = map.get("zone");
            zones.add((String) zoneObj);
            Object clickObj = map.get("click");
            clicks.add((Integer) clickObj);
        }

        String[] zonesList = zones.toArray(new String[zones.size()]);
        Integer[] clicksList = clicks.toArray(new Integer[clicks.size()]);

        SaleBean saleBean = new SaleBean();
        saleBean.setZones(zonesList);
        saleBean.setClicks(clicksList);

        return saleBean;

    }
}
