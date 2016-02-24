一.技术 spring3.2+solrj4.7.1 +lucene4.7.0 <br/>
二.功能 <br/>
1.支持在线创建core ，实现不同模块使用不同的url，例如商品访问http://ip:port/good，文章访问http://ip:port/article <br/>
2.将复杂的solr的查询语法简化,封装成不同的方法，浅显易懂，例如<br/>
	andLike,orLike,andEquals,orEquals,andNotEquals,orNotEquals,andDateLessThan,andDateLessThanOrEqualTo
	等几十种方法<br/>
3.实现crud,分页<br/>
  支持Map 和 bean,添加注解即可指定需要的字段让solr进行分析<br/>
4.暂时支持ik分词<br/>
