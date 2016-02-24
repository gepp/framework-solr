package com.jdk2010.framework.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdk2010.framework.solr.client.SolrClient;
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
        for (int i = 10; i < 15; i++) {
            Product product = new Product();
            product.setId(i);
            product.setBrandId(123);
            product.setIsUp(1);
            product.setPrice(99.99d + new Random().nextDouble());
            product.setProductName("Apple iPhone 6s 64GB aaa玫瑰金色 移动联通电信" + i + "G手机");
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
        List<Product> productList = solrClient.queryForObjectList(
                new SolrKit().andLike("productName", "手机", String.class), Product.class);
        System.out.println("返回结果：" + productList.size());
        for (Product product : productList) {
            System.out.println(product.getProductName());
        }
    }
    
    public void testAddMap(){
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("id",1);
        map.put("brandId",100);
        map.put("isUp",1);
        map.put("price",200d);
        map.put("productName","Apple iPhone 6s 16GB aaa玫瑰金色 移动联通电信");
        map.put("productStore", 2000);
        map.put("upTime", new Date());
        solrClient.saveOrUpdateMap(map);
    }
    
    /**
     * 测试查询List<Map>
     */
    public void testQueryForMapList() {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        List<Map<String,Object>> productList = solrClient.queryForObjectList(
                new SolrKit().andLike("productName", "电信", String.class));
        System.out.println("返回结果：" + productList.size());
        for (Map<String,Object> product : productList) {
            System.out.println(product);
        }
    }

    public static void main(String[] args) {
        SolrBaseTest test = new SolrBaseTest();
        // test.testDelete(10);
        //test.testDeleteAll();
        // test.testAddBeans();
        // test.testQueryForBeanList();
        //test.testAddMap();
        test.testQueryForMapList();
    }
}
