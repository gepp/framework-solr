一.技术 spring3.2+solrj4.7.1 +lucene4.7.0 <br/>
二.功能 <br/>
1.支持在线创建core ，实现不同模块使用不同的url，例如商品访问http://ip:port/good，文章访问http://ip:port/article <br/>
2.将复杂的solr的查询语法简化,封装成不同的方法，浅显易懂，例如<br/>
	andLike,orLike,andEquals,orEquals,andNotEquals,orNotEquals,andDateLessThan,andDateLessThanOrEqualTo
	等几十种方法<br/>
3.实现crud,分页<br/>
  支持Map 和 bean,添加注解即可指定需要的字段让solr进行分析<br/>
4.暂时支持ik分词<br/>

三.使用
	<bean id="solrClient" class="com.jdk2010.framework.solr.client.support.DefaultSolrClient"> <br/>
		<property name="serverUrl" value="http://localhost:8081/solr/good">	<br/>
		</property><br/>
 	</bean><br/>
 	 BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");<br/>
   SolrClient solrClient = factory.getBean(SolrClient.class);<br/>
   solrClient.deleteByKey("id", 10 + "");<br/>
  
  solr安装分2种<br/>
  1.直接运行 java -jar start.jar<br/>
  2.使用tomcat等容器部署<br/>
  步骤如下：<br/>
  http://note.youdao.com/share/?id=296afcd2b7651c20b39643db3fd90b1b&type=note <br/>
  多core 部署<br/>
  如下：
  http://note.youdao.com/share/?id=1fa21109d9bbc00e4e2b0e4ac83b086f&type=note
  <br/>
   
