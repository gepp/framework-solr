package com.jdk2010.framework.test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jdk2010.framework.solr.client.SolrClient;
import com.jdk2010.framework.solr.util.SolrKit;

public class SolrCloudTest {

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext_cloud.xml");
        SolrClient solrClient = factory.getBean(SolrClient.class);
        //solrClient.deleteAll();
        for (int i = 20; i < 30; i++) {
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
        SolrKit kit = new SolrKit()/* .addHighlightField("id").addSortField("id",SolrContants.SORT_DESC) */
        .andLike("productName", "手机", String.class).setRows(100);
        List<Map<String, Object>> productList = solrClient.queryForObjectList(kit);
        System.out.println("返回结果：" + productList.size());
        for (Map<String, Object> product : productList) {
            System.out.println(product);
        }
    }
}
