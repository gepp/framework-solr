һ.���� spring3.2+solrj4.7.1 +lucene4.7.0 <br/>
��.���� <br/>
1.֧�����ߴ���core ��ʵ�ֲ�ͬģ��ʹ�ò�ͬ��url��������Ʒ����http://ip:port/good�����·���http://ip:port/article <br/>
2.�����ӵ�solr�Ĳ�ѯ�﷨��,��װ�ɲ�ͬ�ķ�����ǳ���׶�������<br/>
	andLike,orLike,andEquals,orEquals,andNotEquals,orNotEquals,andDateLessThan,andDateLessThanOrEqualTo
	�ȼ�ʮ�ַ���<br/>
3.ʵ��crud,��ҳ�����򣬷��ع̶�����<br/>
  ֧��Map �� model,���ע�⼴��ָ����Ҫ���ֶ���solr����Analyzer<br/>
4.��ʱ֧��ik�ִ�<br/>
5.ʵ��solrcloud crud<br/>
��.ʹ��
	<bean id="solrClient" class="com.jdk2010.framework.solr.client.support.DefaultSolrClient"> <br/>
		<property name="serverUrl" value="http://localhost:8081/solr/good">	<br/>
		</property><br/>
 	</bean><br/>
 	 
 	 BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");<br/>
   SolrClient solrClient = factory.getBean(SolrClient.class);<br/>
   SolrKit kit=new SolrKit().addHighlightField("productName").addHighlightField("id") <br/>
   							.addSortField("id",SolrContants.SORT_DESC)<br/>
                .andLike("productName", "����õ��", String.class).setRows(4);<br/>
   //����kitʵ����productName��id�ĸ���������id ������ productName like ����õ�� �����ؽ��Ϊ4��<br/>
   //��������ͨ��setHighlightPre��setHighlightPost ��ʵ�� <br/>
   //����List<Map><br/>
   List<Map<String,Object>> productList = solrClient.queryForObjectList(kit);<br/>
   //����List<Project><br/>
   List<Product> productList = solrClient.queryForObjectList(kit, Product.class);<br/>
   //���ط�ҳ
   public Page queryForPageList(SolrKit kit, Page page)<br/>
   //����bean ��ҳ
   public <T> Page queryForPageList(SolrKit kit, Page page, Class<T> clazz)<br/>
 
  solr��װ��2��<br/>
  1.ֱ������ java -jar start.jar<br/>
  2.ʹ��tomcat����������<br/>
  �������£�<br/>
  http://note.youdao.com/share/?id=296afcd2b7651c20b39643db3fd90b1b&type=note <br/>
  ��core ����<br/>
  ���£�
  http://note.youdao.com/share/?id=1fa21109d9bbc00e4e2b0e4ac83b086f&type=note
  <br/>
   
