package com.bawei.springbootfor1705e.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

@Configuration
public class HBaseConfig {

    @Value("${spring.hbase.zookeeperQuorum}")
    private String zookeeperQuorum;

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hbase.zookeeper.quorum",zookeeperQuorum);
        return new HbaseTemplate(conf);
    }
}
