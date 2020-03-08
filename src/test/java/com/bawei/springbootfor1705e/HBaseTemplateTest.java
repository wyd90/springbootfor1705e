package com.bawei.springbootfor1705e;

import com.bawei.springbootfor1705e.bean.Student;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HBaseTemplateTest {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    //测试hbase创建名字空间和表
    //但是这种dml操作其实不应该出现在springboot这种web程序中
    @Test
    public void testNsAndTab() throws IOException {
        Connection conn = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
        Admin admin = conn.getAdmin();

        //创建名字空间
        NamespaceDescriptor ns5 = NamespaceDescriptor.create("ns5").build();
        admin.createNamespace(ns5);

        //建表
        TableName tableName = TableName.valueOf("ns5:t1");
        HTableDescriptor t1 = new HTableDescriptor(tableName);
        HColumnDescriptor f1 = new HColumnDescriptor("f1");
        t1.addFamily(f1);
        admin.createTable(t1);
    }

    //插入数据
    @Test
    public void testPutData() {
        hbaseTemplate.put("ns5:t1","r00001","f1","name", Bytes.toBytes("zhangsan"));
        hbaseTemplate.put("ns5:t1","r00001","f1","age", Bytes.toBytes(18));
        hbaseTemplate.put("ns5:t1","r00001","f1","sex", Bytes.toBytes("man"));
        hbaseTemplate.put("ns5:t1","r00001","f1","score", Bytes.toBytes(77.0d));
    }

    //使用回调接口，可以在table上做任何事
    @Test
    public void testCallBack() {
        hbaseTemplate.execute("ns5:t1", new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface table) throws Throwable {
                Put put = new Put(Bytes.toBytes("r00002"));
                put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"),Bytes.toBytes("lisi"));
                put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("age"),Bytes.toBytes(20));
                put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("sex"),Bytes.toBytes("man"));
                put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("score"),Bytes.toBytes(81.0d));

                Put put1 = new Put(Bytes.toBytes("r00003"));
                put1.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"),Bytes.toBytes("wangwu"));
                put1.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("age"),Bytes.toBytes(22));
                put1.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("sex"),Bytes.toBytes("woman"));
                put1.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("score"),Bytes.toBytes(67.0d));

                table.put(put);
                table.put(put1);
                return null;
            }
        });
    }

    //scan表带条件
    @Test
    public void testScan() {
        Scan scan = new Scan();
        SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
                Bytes.toBytes("f1")
                , Bytes.toBytes("score")
                , CompareFilter.CompareOp.GREATER_OR_EQUAL
                , Bytes.toBytes(80.0d));
        SingleColumnValueFilter filter2 = new SingleColumnValueFilter(
                Bytes.toBytes("f1"),
                Bytes.toBytes("sex"),
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes("woman"));

        FilterList fl = new FilterList(FilterList.Operator.MUST_PASS_ONE, filter1, filter2);
        scan.setFilter(fl);

        List<Student> students = hbaseTemplate.find("ns5:t1", scan, new RowMapper<Student>() {
            @Override
            public Student mapRow(Result result, int rowNum) throws Exception {
                byte[] nameByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
                byte[] ageByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("age"));
                byte[] sexByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("sex"));
                byte[] scoreByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("score"));
                return new Student(Bytes.toString(nameByte),Bytes.toInt(ageByte),Bytes.toString(sexByte),Bytes.toDouble(scoreByte));
            }
        });
        for(Student student : students) {
            System.out.println(student);
        }

    }

    //get单条数据
    @Test
    public void testGet() {
        Student student = hbaseTemplate.get("ns5:t1", "r00002", new RowMapper<Student>() {
            @Override
            public Student mapRow(Result result, int rowNum) throws Exception {
                byte[] nameByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("name"));
                byte[] ageByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("age"));
                byte[] sexByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("sex"));
                byte[] scoreByte = result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("score"));
                return new Student(Bytes.toString(nameByte), Bytes.toInt(ageByte), Bytes.toString(sexByte), Bytes.toDouble(scoreByte));
            }
        });
        System.out.println(student);
    }

    //删除数据
    @Test
    public void testDelete() {
        hbaseTemplate.delete("ns5:t1","r00001","f1");
        hbaseTemplate.delete("ns5:t1","r00002","f1","score");
    }

    @Test
    public void addTestData() {
        hbaseTemplate.execute("ns5:t_click", new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface table) throws Throwable {
                Put put = new Put(Bytes.toBytes("r1"));
                put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("zone"),Bytes.toBytes("北京"));
                put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("click"),Bytes.toBytes(2000));
                table.put(put);

                Put put2 = new Put(Bytes.toBytes("r2"));
                put2.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("zone"),Bytes.toBytes("上海"));
                put2.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("click"),Bytes.toBytes(3211));
                table.put(put2);

                Put put3 = new Put(Bytes.toBytes("r3"));
                put3.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("zone"),Bytes.toBytes("广州"));
                put3.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("click"),Bytes.toBytes(2839));
                table.put(put3);
                return null;
            }
        });
    }
}
