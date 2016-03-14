package com.jdk2010.framework.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.spel.ast.Indexer;

import com.jdk2010.framework.dal.client.DalClient;
import com.jdk2010.framework.solr.client.SolrClient;
import com.jdk2010.framework.solr.util.Page;
import com.jdk2010.framework.solr.util.SolrKit;
import com.jdk2010.framework.util.DbKit;

public class SolrBaseTest {
    /**
     * 测试根据ID删除
     * 
     * @param id
     */
    public void testDelete(Integer id) {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        solrClient.deleteByKey("id", 10 + "");
    }

    /**
     * 测试删除所有
     */
    public void testDeleteAll() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        solrClient.deleteAll();
    }

    /**
     * 测试添加bean
     */
    public void testAddBean() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        Product product = new Product();
        product.setId(2);
        product.setBrandId(1001);
        product.setIsUp(1);
        product.setPrice(99.99d);
        product.setProductName("Apple iPhone 6s 64GB aaa玫瑰金色 移动联通电信4G手机");
        product.setProductStore(2000);
        product.setStar(100);
        product.setUpTime(new Date());
        solrClient.saveOrUpdateBean(product);
    }

    /**
     * 测试添加List<Bean>
     */
    public void testAddBeans() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        for (int i = 15; i < 20; i++) {
            Product product = new Product();
            product.setId(i);
            product.setBrandId(123);
            product.setIsUp(1);
            product.setPrice(99.99d + new Random().nextDouble());
            product.setProductName("三星手机 答复 苹果" + i + "G手机");
            product.setProductStore(2000);
            product.setStar(100);
            product.setUpTime(new Date());
            solrClient.saveOrUpdateBean(product);
        }
    }

    /**
     * 测试查询List<Bean>
     */
    public void testQueryForBeanList() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        SolrKit kit = new SolrKit("productName", true).andLike("productName", "移动", String.class);
        List<Product> productList = solrClient.queryForObjectList(kit, Product.class);
        System.out.println("返回结果：" + productList.size());
        for (Product product : productList) {
            System.out.println(product.getProductName());
        }
    }

    public void testAddMap() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", i);
            map.put("brandId", 100);
            map.put("isUp", 1);
            map.put("price", 200d);
            map.put("productName", "Apple iPhone 6s 16GB aaa玫瑰金色 移动联通电信");
            map.put("productStore", 2000);
            map.put("upTime", new Date());
            solrClient.saveOrUpdateMap(map);
        }
    }

    public void testAddMaps() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", i);
            map.put("brandId", 100);
            map.put("isUp", 1);
            map.put("price", 200d);
            map.put("productName", "Apple iPhone 6s 16GB aaa玫瑰金色 移动联通电信");
            map.put("productStore", 2000);
            map.put("upTime", new Date());
            mapList.add(map);
        }
        solrClient.saveOrUpdateMap(mapList);
    }

    /**
     * 测试查询List<Map>
     */
    public void testQueryForMapList() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        SolrKit kit = new SolrKit()/* .addHighlightField("id").addSortField("id",SolrContants.SORT_DESC) */
        .andLike("product_name", "必备 修身", String.class).setRows(10);
        List<Map<String, Object>> productList = solrClient.queryForObjectList(kit);
        System.out.println("返回结果：" + productList.size());
        for (Map<String, Object> product : productList) {
            System.out.println(product);
        }
    }

    /**
     * 测试添加map 和maps 的性能差别 addMap耗时:19977 ms addMaps耗时:256 ms
     * 
     */
    public void testMapAndMaps() {
        SolrBaseTest test = new SolrBaseTest();
        long starttime1 = System.currentTimeMillis();
        test.testAddMap();
        long endtime1 = System.currentTimeMillis();
        System.out.println("==========================");
        test.testDeleteAll();
        long starttime2 = System.currentTimeMillis();
        test.testAddMaps();
        long endtime2 = System.currentTimeMillis();
        System.out.println("addMap耗时:" + (endtime1 - starttime1) + " ms");
        System.out.println("addMaps耗时:" + (endtime2 - starttime2) + " ms");
    }

    public void testqueryForPageListBean() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        Page page = new Page();
        page.setPageSize(10);
        page.setPageIndex(2);
        Page pageList = solrClient.queryForPageList(new SolrKit().setHighlight(true).addHighlightField("productName")
                .andLike("productName", "金色", String.class), page, Product.class);
        List<Product> productList = pageList.getList();
        for (Product product : productList) {
            System.out.println("id:" + product.getId() + "-name:" + product.getProductName());
        }

    }

    public void testqueryForPageListMap() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        Page page = new Page();
        page.setPageSize(10);
        page.setPageIndex(2);
        Page pageList = solrClient.queryForPageList(new SolrKit().setHighlight(true).addHighlightField("productName")
                .setRows(5).andLike("productName", "", String.class), page);
        List<Map<String, Object>> productList = pageList.getList();
        for (Map<String, Object> product : productList) {
            System.out.println("id:" + product.get("id") + "-name:" + product.get("productName"));
        }
    }

    public void testImportMysql() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        DalClient dalClient = factory.getBean(DalClient.class);
        SolrClient solrClient = factory.getBean(SolrClient.class);

        com.jdk2010.framework.util.Page page = new com.jdk2010.framework.util.Page();
        page.setPageSize(1000);
        for (int i = 1; i < 1000; i++) {
            long starttime1 = System.currentTimeMillis();
            page.setPageIndex(i);
            com.jdk2010.framework.util.Page returnList = dalClient.queryForPageList(new DbKit("select * from product"),
                    page);
            solrClient.saveOrUpdateMap(returnList.getList());
            long endtime1 = System.currentTimeMillis();
            System.out.println("testImportMysql耗时:" + (endtime1 - starttime1) + " ms");
        }
    }

    public void test1() throws SolrServerException {
        // 实例化solrserver，以获取与solrserver的通信
        SolrServer solrserver = new HttpSolrServer("http://10.24.61.17:8081/solr/good");
        // 创建查询参数以及设定的查询参数
        SolrQuery params = new SolrQuery();
        params.set("q", "*:*");
        params.set("qt", "/terms");
        // parameters settings for terms requesthandler
        // 参考（refer to）http://wiki.apache.org/solr/termscomponent
        params.set("terms", "true");
        params.set("terms.fl", "product_name_string");
        // 指定下限
        // params.set("terms.lower", ""); // term lower bounder开始的字符
        // params.set("terms.lower.incl", "true");
        // params.set("terms.mincount", "1");
        // params.set("terms.maxcount", "100");
        // http://localhost:8983/solr/terms?terms.fl=text&terms.prefix=学 //
        // using for auto-completing //自动完成
        //params.set("terms.prefix", "磨砂牛皮");
        params.set("terms.regex", "牛皮+.*");
        params.set("terms.regex.flag", "case_insensitive");
        //
        // params.set("terms.limit", "20");
        // params.set("terms.upper", ""); //结束的字符
        // params.set("terms.upper.incl", "false");
        //
        // params.set("terms.raw", "true");
        params.set("terms.sort", "count");// terms.sort={count|index} -如果count，各种各样的条款术语的频率（最高计数第一）。
                                          // 如果index，索引顺序返回条款。默认是count
        // 查询并获取相应的结果！
        QueryResponse response = solrserver.query(params);
        // 获取相关的查询结果
        if (response != null) {
            TermsResponse termsResponse = response.getTermsResponse();
            if (termsResponse != null) {
                Map<String, List<TermsResponse.Term>> termsMap = termsResponse.getTermMap();
                for (Map.Entry<String, List<TermsResponse.Term>> termsEntry : termsMap.entrySet()) {
                    // System.out.println("Field Name: " + termsEntry.getKey());
                    List<TermsResponse.Term> termList = termsEntry.getValue();
                    System.out.println("termList:返回结果："+termList.size());
                    for (TermsResponse.Term term : termList) {
                        System.out.println(term.getTerm() + " : " + term.getFrequency());
                    }
                }
            }
        }
    }
    
     

    public static void main(String[] args) throws SolrServerException {
        SolrBaseTest test = new SolrBaseTest();
        // test.testDelete(10);
        // test.testDeleteAll();
        // test.testAddBeans();
        // test.testQueryForBeanList();
        // test.testAddMap();
       // test.testQueryForMapList();
        // test.testqueryForPageListBean();
        // test.testqueryForPageListMap();
        // test.testImportMysql();
        test.test1();
    }
}
