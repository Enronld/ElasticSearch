ElasticSearch

> `Lucene是一套信息检索工具包（jar包），不包含搜索引擎系统！`
>
> `包含：索引结构、读写引擎的工具、排序、搜索规则......工具类！`
>
> `ES是基于Lucene做了一些封装和增强`

## 1 ElasticSearch概述

### 1.1 概念

开源的高扩展的==分布式全文检索引擎==

ELK技术：elasticsearch+logstash+kibana

![1621257693462](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621257693462.png)

### 1.2 ES和solr的差别

1. 当单纯地对已有数据进行搜索时，Solr更快。

2. 当实时建立索引时，Solr会产生io阻塞，查询性能较差，ElasticSearch具有明显优势。

3. 随着数据量的增加，Solr的搜索效率会变得更低，而ElasticSearch却没有明显的变化。

   |                        ElasticSearch                         |                             Solr                             |
   | :----------------------------------------------------------: | :----------------------------------------------------------: |
   |                    开箱即用，解压就可以用                    |                          安装稍复杂                          |
   |                  自身带有分布式协调管理功能                  |                 利用Zookeeper进行分布式管理                  |
   |                      仅支持json文件格式                      |              支持更多格式数据，如JSON、XML、CSV              |
   |            更注重于核心功能，高级功能多由插件提供            |                      官方提供的功能更多                      |
   | 建立索引快，实时性查询快，用于facebook、新浪等搜索（==新兴实时搜索应用==） | 查询快，更新索引时慢，用于电商等查询多的应用（==传统搜索应用==） |
   |          开发维护者较少，更新太快，学习使用成本较高          | 比较成熟，有一个更大，更成熟的用户、开发和贡献者社区ElasticSearch安装 |

##   2 ElasticSearch安装

> 声明：JDK1.8、ElasticSearch客户端、界面工具
>
> Java开发，版本对应，JDK环境是正常
>
> 下载：http://www.elastic.co/

### 2.1 安装es

![1621242474343](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621242474343.png)

1. 解压就可以使用

   ![1621245666686](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621245666686.png)

2. 熟悉目录

   bin 启动文件

   config 配置文件

   ​	log4j2   日志配置文件

   ​	jvm.options   java 虚拟机相关的配置

   ​	elasticsearch.yml   elasticsearch的配置文件![1621245898572](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621245898572.png)

   lib 相关jar包

   logs 日志

   modules 功能模块

   plugins 插件

3. 启动 访问9200

   ![1621246041104](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621246041104.png)

![1621246418609](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621246418609.png)

4. 访问测试

   ![1621246604827](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621246604827.png)

### 2.2 安装es head

> 安装可视化界面 es head的插件

1. 下载地址：https://github.com/mobz/elasticsearch-head/

2. ![1621255858578](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621255858578.png)

   ![1621256049185](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621256049185.png)

![1621256161428](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621256161428.png)

```解决跨域问题    修改elasticsearch.yml   重启elasticsearch```

![1621256300496](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621256300496.png)

![1621256491365](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621256491365.png)

![1621256819699](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621256819699.png)

==head就把它当做数据展示工具。后面所有的查询用kibana==

### 2.3 安装kibana

> kibana是一个针对Elasticsearch的开源分析及可视化平台，用来搜索、查看交互存储在Elasticsearch索引中的数据。
>
> 官网：htpps://www.elastic.co/cn/kibana
>
> 注意：kibana下载版本要与elasticsearch一致

1. 解压后的目录

![1621259064542](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259064542.png)

2. 启动

   ![1621259294296](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259294296.png)

3. 访问测试

   ![1621259416008](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259416008.png)

4. 开发工具

![1621259512429](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259512429.png)

5. 汉化

   ![1621259606610](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259606610.png)

![1621259689071](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259689071.png)

![1621259666430](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621259666430.png)

##   3 ES核心概念

> elasticsearch是==面向文档==  一切都是json

| Relational DB    | ElasticSearch         |
| ---------------- | --------------------- |
| 数据库(database) | 索引(indices)         |
| 表(tables)       | types（慢慢会被弃用） |
| 行(rows)         | documents             |
| 字段(columns)    | fields                |

### 3.1 概念

elasticsearch(集群)中可以包含多个索引(数据库),每个索引中可以包含多个类型(表)，每个类型又包含多个文档(行)，每个文档中又包含多个字段(列)

1. 文档

- 自我包含，一篇文档同时包含字段和对应的值，也就是同时包含key:value
- 可以是层次型的，一个文档中包含自文档
- 灵活的结构，文档不依赖预先定义的模式

2. 类型

- 文档的逻辑容器，就像关系型数据库一样，表格是行的容器。类型中对于字段的定义称为映射。

3.  索引

   索引是映射类型的容器，elasticsearch中的索引是一个非常大的文档集合。

### 3.2 节点和分片

一个集群至少有一个节点，而一个节点就是一个elasticsearch进程，节点可以有多个索引默认的，索引的每一个主分片会有一个副本(replica shard 复制分片)

![1621326984778](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621326984778.png)

主分片对应的复制分片都不会在同一个节点内，这样有利于某个节点挂掉了，数据也不至于丢失。

一个分片是一个Lucene索引，一个包含倒排索引的文件目录。

### 3.3 倒排索引

> 适用于快速的全文搜索

| 博客文章（原始数据） |              | 索引列表（倒排索引） |            |
| -------------------- | ------------ | -------------------- | ---------- |
| 博客文章ID           | 标签         | 标签                 | 博客文章ID |
| 1                    | python       | python               | 1,2,3      |
| 2                    | python       | linux                | 3,4        |
| 3                    | linux,python |                      |            |
| 4                    | linux        |                      |            |

==一个elasticsearch索引是由多个Lucene索引组成==

## 4 IK分词器插件

> 分词：把一段中文或者别的划分成一个个的关键字，我们在搜索时会把自己的信息进行分词，把数据库中或者索引库中数据进行分词，然后进行一个匹配操作。
>
> IK提供了两个分词算法:ik_smart(最少切分)和ik_max_word(最细粒度划分)

### 4.1 安装

1. 地址：https://github.com/medcl/elasticsearch-analysis-ik

2. 解压到elasticsearch下的plugins下

![1621328651029](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621328651029.png)

3. 重启elasticsearch,可以看到plugin被加载

![1621328783061](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621328783061.png)

### 4.2 测试

![1621329187781](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621329187781.png)

![1621329242198](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621329242198.png)

自己需要的词需要自己加到分词器的字典中

![1621329717973](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621329717973.png)

重启

![1621330142322](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621330142322.png)

![1621330656011](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621330656011.png)

## 5 Rest风格说明

> 一组软件架构风格，提供了一组设计原则和约束条件。

基本Rest命令说明：

| method |                     url地址                     |          描述          |
| :----: | :---------------------------------------------: | :--------------------: |
|  PUT   |     localhost:9200/索引名称/类型名称/文档id     | 创建文档（指定文档id） |
|  POST  |        localhost:9200/索引名称/类型名称         | 创建文档（随机文档id） |
|  POST  | localhost:9200/索引名称/类型名称/文档id/_update |        修改文档        |
| DELETE |     localhost:9200/索引名称/类型名称/文档id     |        删除文档        |
|  GET   |     localhost:9200/索引名称/类型名称/文档id     |   通过文档id查询文档   |
|  POST  |    localhost:9200/索引名称/类型名称/_search     |      查询所有数据      |

## 6 关于索引基本操作

### 6.1 创建索引

1. 创建一个索引

```
PUT  /索引名/~类型名~/文档id 
{请求体}
```

![1621341283419](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621341283419.png)

![1621341363704](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621341363704.png)

```
基本数据类型

1. 字符串类型 : text、keyword
2. 数值类型 : long,Integer,short,byte,double,float,half_float,scaled_float
3. 日期类型 : date
4. 布尔值类型 : boolean
5. 二进制类型 : binary
```

2. 指定字段类型

![1621342022678](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621342022678.png)

![1621342093894](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621342093894.png)

3. 不指定字段类型，es默认配置字段类型

![1621342291327](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621342291327.png)

![1621342319781](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621342319781.png)

4. 扩展：通过get _cat/可以获得es的当前的很多信息

![1621342561417](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621342561417.png)

### 6.2 修改索引

1. 直接使用PUT覆盖

![1621343247851](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621343247851.png)

2. 使用POST方式进行修改

   ![1621343449715](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621343449715.png)

### 6.3 删除索引

根据请求来判断是删除索引还是删除文档记录

![1621343529638](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621343529638.png)

![1621343545303](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621343545303.png)

## 7 关于文档的基本操作(重点)

### 7.1 基本操作

1. 添加数据

   ![1621344606851](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621344606851.png)

![1621344898767](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621344898767.png)

![1621345247058](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621345247058.png)

2. 获取数据

   ![1621345303673](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621345303673.png)

   ![1621346022222](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621346022222.png)

3. 更新数据 

   PUT

   ![1621345380374](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621345380374.png)

   POST  _update,==推荐==

   ![1621345576495](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621345576495.png)

### 7.2 复杂操作

![1621408028966](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621408028966.png)

![1621408338573](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621408338573.png)

1. 简单查询

![1621408542195](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621408542195.png)

2. 结果的过滤

![1621408721379](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621408721379.png)

3. 排序

   ![1621409010250](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621409010250.png)

4. 分页查询

   ![1621409188965](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621409188965.png)

5. 布尔值查询

   - must (and) ： 所有条件都要符合

   ![1621409435446](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621409435446.png)

   - should (or)

   ![1621409576349](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621409576349.png)

   - must_not (not)

   ![1621409672230](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621409672230.png)

   - 过滤器

   ![1621409930445](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621409930445.png)

6. 匹配多个条件

   ![1621410205899](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621410205899.png)

7. 精确查询

   ```
   term查询是直接通过倒排索引指定的词条进程精确地查找
   关于分词：
   term 直接查询精确的； match 会使用分词器解析（先分析文档，然后再通过分析的文档进行查询）
   两个类型 text keyword
   ```

   ![1621410959605](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621410959605.png)

   ![1621411180680](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621411180680.png)

8. 多个值匹配精确查询

   ![1621411424987](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621411424987.png)

9. 高亮查询

   ![1621411815377](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621411815377.png)

   ![1621411947032](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621411947032.png)

## 8 集成SpringBoot

找官方文档 https://www.elastic.co/guide/en/elasticsearch/client/index.html

![1621413542981](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621413542981.png)

​	![1621413614639](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621413614639.png)

### 8.1 环境准备

1. 找到原生的依赖

   ```xml
   <dependency>
       <groupId>org.elasticsearch.client</groupId>
       <artifactId>elasticsearch-rest-high-level-client</artifactId>
       <version>7.6.2</version>
   </dependency>
   ```

2. 找对象

   ![1621413979880](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621413979880.png)

3. 分析这个类中的方法

   > 配置基本的项目

   1. 创建空项目

   ![1621414352779](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621414352779.png)

   2. 创建module

   ![1621414618503](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621414618503.png)

   ![1621414684215](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621414684215.png)

   3. 版本对应

   ![1621424453236](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621424453236.png)

   ```
   修改版本号为7.6.1
   ```

   ![1621425463792](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621425463792.png)

   4. 源码检索

   ![1621426717142](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621426717142.png)

   核心类就一个

   ```java
   //
   // Source code recreated from a .class file by IntelliJ IDEA
   // (powered by Fernflower decompiler)
   //
   
   package org.springframework.boot.autoconfigure.elasticsearch.rest;
   
   import java.time.Duration;
   import org.apache.http.HttpHost;
   import org.apache.http.auth.AuthScope;
   import org.apache.http.auth.Credentials;
   import org.apache.http.auth.UsernamePasswordCredentials;
   import org.apache.http.client.CredentialsProvider;
   import org.apache.http.impl.client.BasicCredentialsProvider;
   import org.elasticsearch.client.RestClient;
   import org.elasticsearch.client.RestClientBuilder;
   import org.elasticsearch.client.RestHighLevelClient;
   import org.springframework.beans.factory.ObjectProvider;
   import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
   import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
   import org.springframework.boot.context.properties.PropertyMapper;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   class RestClientConfigurations {
       RestClientConfigurations() {
       }
   
       @Configuration(
           proxyBeanMethods = false
       )
       static class RestClientFallbackConfiguration {
           RestClientFallbackConfiguration() {
           }
   
           @Bean
           @ConditionalOnMissingBean
           RestClient elasticsearchRestClient(RestClientBuilder builder) {
               return builder.build();
           }
       }
   
       @Configuration(
           proxyBeanMethods = false
       )
       @ConditionalOnClass({RestHighLevelClient.class})
       static class RestHighLevelClientConfiguration {
           RestHighLevelClientConfiguration() {
           }
   
           @Bean
           @ConditionalOnMissingBean
           RestHighLevelClient elasticsearchRestHighLevelClient(RestClientBuilder restClientBuilder) {
               return new RestHighLevelClient(restClientBuilder);
           }
   
           @Bean
           @ConditionalOnMissingBean
           RestClient elasticsearchRestClient(RestClientBuilder builder, ObjectProvider<RestHighLevelClient> restHighLevelClient) {
               RestHighLevelClient client = (RestHighLevelClient)restHighLevelClient.getIfUnique();
               return client != null ? client.getLowLevelClient() : builder.build();
           }
       }
   
       @Configuration(
           proxyBeanMethods = false
       )
       static class RestClientBuilderConfiguration {
           RestClientBuilderConfiguration() {
           }
   
           @Bean
           @ConditionalOnMissingBean
           RestClientBuilder elasticsearchRestClientBuilder(RestClientProperties properties, ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
               HttpHost[] hosts = (HttpHost[])properties.getUris().stream().map(HttpHost::create).toArray((x$0) -> {
                   return new HttpHost[x$0];
               });
               RestClientBuilder builder = RestClient.builder(hosts);
               PropertyMapper map = PropertyMapper.get();
               map.from(properties::getUsername).whenHasText().to((username) -> {
                   CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                   Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword());
                   credentialsProvider.setCredentials(AuthScope.ANY, credentials);
                   builder.setHttpClientConfigCallback((httpClientBuilder) -> {
                       return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                   });
               });
               builder.setRequestConfigCallback((requestConfigBuilder) -> {
                   properties.getClass();
                   map.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis).to(requestConfigBuilder::setConnectTimeout);
                   properties.getClass();
                   map.from(properties::getReadTimeout).whenNonNull().asInt(Duration::toMillis).to(requestConfigBuilder::setSocketTimeout);
                   return requestConfigBuilder;
               });
               builderCustomizers.orderedStream().forEach((customizer) -> {
                   customizer.customize(builder);
               });
               return builder;
           }
       }
   }
   ```

### 8.2 api测试

1. 创建索引

2. 获取索引，只能判断其是否存在

3. 删除索引

4. 创建文档

5. 获取文档

6. 更新文档信息

7. 删除文档记录

8. 批量插入

9. 查询

   ```
   说明
   ~term查询：查询某一个字段进行完全匹配一个条件，也就是不进行分词匹配；
   ~terms查询：查询某一个字段进行完全匹配，但是可以匹配多个条件A1条件或者A2条件，A1 or A2 …
   ~match_all查询：查询某一个表的所有内容；
   ~match查询：查询某一个字段，进行分词匹配；分词关系是”或”的关系；
   ~布尔match查询：查询某一个字段，进行分词匹配；分词关系可以进行指定是”或”还是”与”；
   ```

   ```java
   package com.enron;
   
   import com.alibaba.fastjson.JSON;
   import com.enron.pojo.User;
   import org.apache.lucene.util.QueryBuilder;
   import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
   import org.elasticsearch.action.bulk.BulkRequest;
   import org.elasticsearch.action.bulk.BulkResponse;
   import org.elasticsearch.action.delete.DeleteRequest;
   import org.elasticsearch.action.delete.DeleteResponse;
   import org.elasticsearch.action.get.GetRequest;
   import org.elasticsearch.action.get.GetResponse;
   import org.elasticsearch.action.index.IndexRequest;
   import org.elasticsearch.action.index.IndexResponse;
   import org.elasticsearch.action.search.SearchRequest;
   import org.elasticsearch.action.search.SearchResponse;
   import org.elasticsearch.action.support.master.AcknowledgedResponse;
   import org.elasticsearch.action.update.UpdateAction;
   import org.elasticsearch.action.update.UpdateRequest;
   import org.elasticsearch.action.update.UpdateResponse;
   import org.elasticsearch.client.RequestOptions;
   import org.elasticsearch.client.RestHighLevelClient;
   import org.elasticsearch.client.indices.CreateIndexRequest;
   import org.elasticsearch.client.indices.CreateIndexResponse;
   import org.elasticsearch.client.indices.GetIndexRequest;
   import org.elasticsearch.client.indices.GetIndexResponse;
   import org.elasticsearch.common.unit.TimeValue;
   import org.elasticsearch.common.xcontent.XContentType;
   import org.elasticsearch.index.engine.Engine;
   import org.elasticsearch.index.query.MatchAllQueryBuilder;
   import org.elasticsearch.index.query.MatchQueryBuilder;
   import org.elasticsearch.index.query.QueryBuilders;
   import org.elasticsearch.index.query.TermQueryBuilder;
   import org.elasticsearch.search.SearchHit;
   import org.elasticsearch.search.builder.SearchSourceBuilder;
   import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
   import org.junit.jupiter.api.Test;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.beans.factory.annotation.Qualifier;
   import org.springframework.boot.test.context.SpringBootTest;
   import sun.reflect.ReflectionFactory;
   
   import javax.naming.directory.SearchResult;
   import java.io.IOException;
   import java.util.ArrayList;
   import java.util.concurrent.TimeUnit;
   
   
   /**
    * es7.6.X高级客户端测试API
    */
   
   @SpringBootTest
   class EnrondEsApiApplicationTests {
   
   	//面向对象来操作
   	@Autowired
   	@Qualifier("restHighLevelClient")
   	private RestHighLevelClient client;
   
   	//1 测试索引的创建 Request
   	@Test
   	void testCreateIndex() throws IOException {
   		//1 创建索引请求
   		CreateIndexRequest request = new CreateIndexRequest("enron_index");
   		//2 客户端执行创建请求 IndicesClient,请求后获得响应
   		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
   		System.out.println(createIndexResponse);
   	}
   
   	//2 测试获取索引
   	@Test
   	void testExistIndex() throws IOException {
   	 	GetIndexRequest request = new GetIndexRequest("enron_index");
   		Boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
   		System.out.println(exists);
   	}
   
   	//3 测试删除索引
   	@Test
   	void testDeleteIndex() throws IOException {
   		DeleteIndexRequest request = new DeleteIndexRequest("enron_index");
   		AcknowledgedResponse delete = client.indices().delete(request,RequestOptions.DEFAULT);
   		System.out.println(delete);
   	}
   
   	//4 测试添加文档
   	@Test
   	void testAddDocument() throws IOException {
   		//创建对象
   		User user = new User("妲己",3);
   		//创建请求
   		IndexRequest request = new IndexRequest("enron_index");
   
   		//规则 put enron_index/_doc/1
   		request.id("1");
   //		request.timeout(TimeValue.timeValueSeconds(1));
   
   		//将数据放入请求 json
   		IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);
   
   		//客户端发送请求,获取响应的结果
   		IndexResponse indexResponse = client.index(source,RequestOptions.DEFAULT);
   		System.out.println(indexResponse.toString());
   		System.out.println(indexResponse.status());		//对应命令返回的状态 CREATED
   
   	}
   
   	//5 获取文档，判断是否存在 get enron_index/doc/1
   	void testIsExits() throws IOException {
   		GetRequest getRequest = new GetRequest("enron_index","1");
   		//不获取返回的 _source的上下文了
   		getRequest.fetchSourceContext(new FetchSourceContext(false));
   		getRequest.storedFields("_none_");
   
   		Boolean exists = client.exists(getRequest,RequestOptions.DEFAULT);
   		System.out.println(exists);
   	}
   
   	// 获取文档的信息
   	@Test
   	void testGetDocument() throws IOException {
   		GetRequest getRequest = new GetRequest("enron_index","1");
   
   		GetResponse getResponse = client.get(getRequest,RequestOptions.DEFAULT);
   		System.out.println(getResponse.getSourceAsString());		//打印文档的内容
   		System.out.println(getResponse);
   	}
   
   	//6 更新文档的信息
   	@Test
   	void testUpdateDocument() throws IOException {
   		UpdateRequest updateRequest = new UpdateRequest("enron_index","1");
   
   		User user = new User("妲己爱Java",18);
   		updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
   
   		UpdateResponse updateResponse = client.update(updateRequest,RequestOptions.DEFAULT);
   		System.out.println(updateResponse.status());
   	}
   
   	//7 删除文档记录
   	@Test
   	void testDeleteDocument() throws IOException {
   		DeleteRequest deleteRequest = new DeleteRequest("enron_index", "1");
   
   		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
   		System.out.println(deleteResponse.status());
   	}
   
   	//8 特殊 项目一般会批量插入数据
   	@Test
   	void testBulkRequest() throws IOException {
   		BulkRequest bulkRequest = new BulkRequest();
   		bulkRequest.timeout("10s");
   
   		ArrayList<User> userList = new ArrayList<>();
   
   		userList.add(new User("妲己1",3));
   		userList.add(new User("妲己2",3));
   		userList.add(new User("妲己3",3));
   		userList.add(new User("后羿1",3));
   		userList.add(new User("后羿2",3));
   		userList.add(new User("后羿3",3));
   
   		//批处理请求
   		for (int i = 0; i < userList.size(); i++){
   			bulkRequest.add(new IndexRequest("enron_index")
   							.id(""+(i+1))						//不设置id有默认的随机id
   							.source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
   		}
   		BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);
   		System.out.println(bulkResponse.hasFailures());	//是否失败 返回false代表成功
   	}
   
   	//9 查询
   	//SearchRequest 搜索请求
   	//SearchSourceBuilder 条件构造
   	//HighlightBuilder 构建高亮
   	//TermQueryBuilder 精确查询...
   	@Test
   	void testSearch() throws IOException {
   		SearchRequest searchRequest = new SearchRequest("enron_index");
   		//构建搜索条件
   		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
   
   		//查询条件  使用QueryBuilders工具类来实现
   		//QueryBuilders.termQuery精确
   		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name","1");
   		//QueryBuilders.matchAllQuery匹配所有
   //		MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
   		searchSourceBuilder.query(termQueryBuilder);
   		//分页
   //		searchSourceBuilder.from();
   //		searchSourceBuilder.size();
   		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
   		searchRequest.source(searchSourceBuilder);
   
   		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
   		System.out.println(JSON.toJSONString(searchResponse.getHits()));
   		System.out.println("===================================");
   		for (SearchHit documentFields: searchResponse.getHits().getHits()){
   			System.out.println(documentFields.getSourceAsMap());
   		}
   	}
   
   }
   
   ```

   ==注意：中文查询需要用分词器==

### 8.2 集成api完成简易数据库管理工具demo

1. 前端：创建vue-cli项目

   ![1621413979880](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1621673398443.png)

2. 后端：在前面springboot的基础上完成controller层和service层

   核心代码(其余代码见代码包)

   ```java
   @Service
   public class ElasticsearchOptionService {
       @Autowired
       @Qualifier("restHighLevelClient")
       private RestHighLevelClient client;
       private static long documentNum;
       //查看索引列表
       public List<String> getAllIndices() throws Exception {
           // 构建请求,注意*号的写法
           GetIndexRequest getIndexRequest = new GetIndexRequest("*");
   
           // 构建获取所有索引的请求：org.elasticsearch.client.indices.GetIndexRequest
           GetIndexResponse getIndexResponse = client.indices().get(getIndexRequest, RequestOptions.DEFAULT);
   
           // 获取所有的索引
           String[] indices = getIndexResponse.getIndices();
           List<String> asList = Arrays.asList(indices);
           System.out.println(asList);
           return asList;
       }
   
       //获取索引是否存在
       private Boolean testExistIndex(String indexName) throws IOException {
           GetIndexRequest request = new GetIndexRequest(indexName);
           Boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
           System.out.println(exists);
           return exists;
       }
   
       //创建索引
       public Boolean createIndex(String indexName) throws Exception {
           //1 创建索引请求
           CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
           //2 客户端执行创建请求 IndicesClient,请求后获得响应
           CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
           System.out.println(createIndexResponse);
           return testExistIndex(indexName);
       }
   
       //删除索引
       public Boolean deleteIndex(String indexName) throws IOException {
           DeleteIndexRequest request = new DeleteIndexRequest(indexName);
           AcknowledgedResponse delete = client.indices().delete(request,RequestOptions.DEFAULT);
           System.out.println(delete);
   
           return testExistIndex(indexName);
   
       }
   
       //添加文档
       public String addDocument(String indexName,String id,List<String> key, List<String> value) throws IOException {
           //创建对象
           Map<String,String> map = new HashMap<String, String>();
           for (int i = 0; i < key.size(); i++){
               map.put(key.get(i),value.get(i));
           }
   
           //创建请求
           IndexRequest request = new IndexRequest(indexName);
           request.id(id);
   
           //将数据放入请求 json
           IndexRequest source = request.source(JSON.toJSONString(map), XContentType.JSON);
   
           //客户端发送请求,获取响应的结果
           IndexResponse indexResponse = client.index(source,RequestOptions.DEFAULT);
   
           return indexResponse.status().toString();  //成功OK
       }
   
       //判断文档是否存在
       private Boolean DocumentIsExits(String indexName,String id) throws IOException {
           GetRequest getRequest = new GetRequest(indexName,id);
           //不获取返回的 _source的上下文了
           getRequest.fetchSourceContext(new FetchSourceContext(false));
           getRequest.storedFields("_none_");
   
           Boolean exists = client.exists(getRequest,RequestOptions.DEFAULT);
           System.out.println(exists);
           return exists;
       }
   
   
       //获取指定搜索条件搜索文档的数量 供前端分页
       //flag为0表示全部，1表示部分
       public long getSearchDocumentNum(){
           return documentNum;
       }
   
       //获取文档信息
       //flag标志整个查询还是条件查询
       public List<Document> getDocument(String indexName,Integer flag, String search, String field,Integer pageNo,Integer pageSize) throws IOException {
           SearchRequest searchRequest = new SearchRequest(indexName);
           SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
           if (flag == 0){
               //匹配所有
               MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
               //分页
               searchSourceBuilder.query(matchAllQueryBuilder).from(pageNo).size(pageSize);
           }else{
               //多条件匹配（不会分词）
               MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(search,field);
               searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)).query(multiMatchQueryBuilder).from(pageNo).size(pageSize);
           }
   
           searchRequest.source(searchSourceBuilder);
   
           SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
           documentNum = searchResponse.getHits().getTotalHits().value;
           List<Document> documents = new ArrayList<>();
   
           for (SearchHit documentFields: searchResponse.getHits().getHits()){
   
               Document document = new Document(documentFields.getId(),documentFields.getSourceAsMap());
               System.out.println(documentFields.getSourceAsMap());
               documents.add(document);
           }
   
           return documents;
       }
   
       //更新文档信息
       public String updateDocument(String indexName,String id,List<String> key, List<String> value) throws IOException {
           UpdateRequest updateRequest = new UpdateRequest(indexName,id);
   
           Map<String,String> map = new HashMap<String, String>();
           for (int i = 0; i < key.size(); i++){
               map.put(key.get(i),value.get(i));
           }
           updateRequest.doc(JSON.toJSONString(map),XContentType.JSON);
   
           UpdateResponse updateResponse = client.update(updateRequest,RequestOptions.DEFAULT);
           System.out.println(updateResponse.status());
           return updateResponse.status().toString();
       }
   
       //删除文档记录
       public Boolean deleteDocument(String indexName, String id) throws IOException {
           DeleteRequest deleteRequest = new DeleteRequest(indexName,id);
           DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
           System.out.println(deleteResponse.status());
   
           return DocumentIsExits(indexName,id);
       }
   
       //批量导入文档
       public Boolean uploadDocument(File file) throws Exception {
           BulkRequest bulkRequest = new BulkRequest();
           bulkRequest.timeout("10s");
   
           Excel excel = new Excel();
           List<List<String>> all = excel.readAllRows(file);
   
           ArrayList<Map> documents = new ArrayList<>();
           for (int i = 1; i < all.size(); i++){
               Map<String,String> map = new HashMap<String, String>();
               for (int j = 0; j < all.get(0).size(); j++){
                   map.put(all.get(0).get(j),all.get(i).get(j));
               }
               documents.add(map);
   
           }
   
           //批处理请求
           for (int i = 0; i < documents.size(); i++){
               bulkRequest.add(new IndexRequest("enron_index")
                       .id(""+(i+1))                 //不设置id有默认的随机id
                       .source(JSON.toJSONString(documents.get(i)),XContentType.JSON));
           }
           BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);
           System.out.println(bulkResponse.hasFailures());    //是否失败 返回false代表成功
           return bulkResponse.hasFailures();
       }
   }
   ```

3. 前后端跨域可以用==nginx反向代理==

   ![1622019680868](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1622019680868.png)

4. 效果

   ![1622020086173](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1622020086173.png)

## 9 实战（京东商城书籍）

### 9.1 爬虫

> 数据问题
>
> 数据库中获取、消息队列中获取、爬虫

1. 获取请求返回的页面信息，筛选出我们想要的数据。

   ```java
   package com.enron.utils;
   
   import com.enron.pojo.Content;
   import org.jsoup.Jsoup;
   import org.jsoup.nodes.Document;
   import org.jsoup.nodes.Element;
   import org.jsoup.select.Elements;
   import org.springframework.stereotype.Component;
   import java.net.URL;
   import java.util.ArrayList;
   import java.util.List;
   
   @Component
   public class HtmlParseUtil {
       public List<Content> parseJD(String keywords) throws Exception {
           //获取请求 https://search.jd.com/Search?keyword=java
           //前提：需要联网
           String url = "https://search.jd.com/Search?keyword=" + keywords;
           //解析网页(返回Document就是浏览器Document对象)
           Document document = Jsoup.parse(new URL(url),30000);
           //所有js中使用的方法这里都可以用
           Element element = document.getElementById("J_goodsList");
           System.out.println(element.html());
           //获取所有的li元素
           Elements elements = element.getElementsByTag("li");
   
           ArrayList<Content> goodsList = new ArrayList<>();
           //获取元素中的内容，这里的el就是每一个li标签了
           for(Element el:elements){
               //关于图片特别多的网站，所有图片都是延迟加载的 source-data-lazy-img
               String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
               String price = el.getElementsByClass("p-price").eq(0).text();
               String title = el.getElementsByClass("p-name").eq(0).text();
   
               Content content = new Content();
               content.setTitle(title);
               content.setImg(img);
               content.setPrice(price);
   
               goodsList.add(content);
           }
           return goodsList;
       }
   }
   ```

### 9.2 前后端分离

1. 前端导入

   ![1622020490392](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1622020490392.png)

2. 后端主要业务实现(所有代码在代码包)

```java
package com.enron.service;

import com.alibaba.fastjson.JSON;
import com.enron.pojo.Content;
import com.enron.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//业务编写
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //1. 解析数据放入es索引中
    public Boolean parseContent(String keywords) throws Exception {
        List<Content> contents = new HtmlParseUtil().parseJD(keywords);
        //把查询出来的数据放入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for (int i = 0; i < contents.size(); i++){
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    //2. 获取这些数据实现搜索功能
    public List<Map<String,Object>> searchPage(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo <= 1){
            pageNo = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);      //重复多个高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(SearchHit documentFields : searchResponse.getHits().getHits()){

            Map<String, HighlightField> highlightFields =  documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String,Object> sourceAsMap = documentFields.getSourceAsMap();       //原来的记录
            //解析高亮的字段
            if (title != null){
                Text[] fragments = title.fragments();
                //将高亮字段替换原来的字段
                String n_title = "";
                for(Text text : fragments){
                    n_title += text;
                }
                sourceAsMap.put("title",n_title);   //高亮字段替换掉原来的内容
            }
            list.add(sourceAsMap);

            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

}
```

### 9.3 搜索高亮

1. 首先调用接口parse将爬取数据存入es

   ![1622020793044](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1622020793044.png)

2. 实现高亮搜索

   ![1622020822509](https://raw.githubusercontent.com/Enronld/ElasticSearch/main/images/1622020822509.png)