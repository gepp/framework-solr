package com.jdk2010.framework.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdk2010.framework.solr.client.SolrClient;
import com.jdk2010.framework.solr.constant.SolrContants;
import com.jdk2010.framework.solr.util.Page;
import com.jdk2010.framework.solr.util.SolrKit;

public class SolrBaseTest {
    /**
     * 测试根据ID删除
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
        SolrKit kit=new SolrKit("productName",true).andLike("productName", "三星", String.class);
        List<Product> productList = solrClient.queryForObjectList(kit, Product.class);
        System.out.println("返回结果：" + productList.size());
        for (Product product : productList) {
            System.out.println(product.getProductName());
        }
    }
    
    public void testAddMap(){
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        for(int i=0;i<100;i++){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("id",i);
        map.put("brandId",100);
        map.put("isUp",1);
        map.put("price",200d);
        map.put("productName","Apple iPhone 6s 16GB aaa玫瑰金色 移动联通电信");
        map.put("productStore", 2000);
        map.put("upTime", new Date());
        solrClient.saveOrUpdateMap(map);
        } 
    }
    
    public void testAddMaps(){
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        List<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
        for(int i=0;i<100;i++){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("id",i);
            map.put("brandId",100);
            map.put("isUp",1);
            map.put("price",200d);
            map.put("productName","Apple iPhone 6s 16GB aaa玫瑰金色 移动联通电信");
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
        SolrKit kit=new SolrKit("productName",true).addHighlightField("id").addSortField("id",SolrContants.SORT_DESC)
                .andLike("productName", "电信玫瑰", String.class).setRows(4);
        List<Map<String,Object>> productList = solrClient.queryForObjectList(kit);
        System.out.println("返回结果：" + productList.size());
        for (Map<String,Object> product : productList) {
            System.out.println(product);
        }
    }
    /**
     * 测试添加map 和maps 的性能差别
     * addMap耗时:19977 ms
     * addMaps耗时:256 ms
     * 
     */
    public void testMapAndMaps(){
        SolrBaseTest test = new SolrBaseTest();
        long starttime1=System.currentTimeMillis();
        test.testAddMap();
        long endtime1=System.currentTimeMillis();
        System.out.println("==========================");
        test.testDeleteAll();
        long starttime2=System.currentTimeMillis();
        test.testAddMaps();
        long endtime2=System.currentTimeMillis();
        System.out.println("addMap耗时:"+(endtime1-starttime1)+" ms");
        System.out.println("addMaps耗时:"+(endtime2-starttime2)+" ms");
    }
    
    public void testqueryForPageListBean(){
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        Page page=new  Page();
             page.setPageSize(10);
             page.setPageIndex(2);
             Page pageList = solrClient.queryForPageList(new SolrKit().setHighlight(true).addHighlightField("productName").andLike("productName", "金色", String.class),page, Product.class);
             List<Product> productList=pageList.getList();
             for(Product product:productList){
                 System.out.println("id:"+product.getId()+"-name:"+product.getProductName());
             }
             
    }
    
    public void testqueryForPageListMap(){
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        Page page=new  Page();
             page.setPageSize(10);
             page.setPageIndex(2);
             Page pageList = solrClient.queryForPageList(new SolrKit().setHighlight(true).addHighlightField("productName").setRows(5).andLike("productName", "金色 联通电信", String.class),page);
             List<Map<String,Object>> productList=pageList.getList();
             for(Map<String,Object> product:productList){
                 System.out.println("id:"+product.get("id")+"-name:"+product.get("productName"));
             }
    }

    public static void main(String[] args) {
        SolrBaseTest test = new SolrBaseTest();
        // test.testDelete(10);
        //test.testDeleteAll();
        // test.testAddBeans();
         test.testQueryForBeanList();
        //test.testAddMap();
//        test.testQueryForMapList();
//        test.testqueryForPageListBean();
//        test.testqueryForPageListMap();
            
    }
}
