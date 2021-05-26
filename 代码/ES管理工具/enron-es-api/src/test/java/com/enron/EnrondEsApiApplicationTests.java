package com.enron;

import com.alibaba.fastjson.JSON;
import com.enron.pojo.User;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
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
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import sun.reflect.ReflectionFactory;

import javax.naming.directory.SearchResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

	//获取所有索引
	@Test
	void getAllIndices() throws IOException {
		// 构建请求,注意*号的写法
		GetIndexRequest getIndexRequest = new GetIndexRequest("*");
//		System.out.println(getIndexRequest);

		// 构建获取所有索引的请求：org.elasticsearch.client.indices.GetIndexRequest
		GetIndexResponse getIndexResponse = client.indices().get(getIndexRequest, RequestOptions.DEFAULT);

		// 获取所有的索引
		String[] indices = getIndexResponse.getIndices();
		List<String> asList = Arrays.asList(indices);
		System.out.println(asList);
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
		System.out.println(JSON.toJSONString(user));
		IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);

		//客户端发送请求,获取响应的结果
		IndexResponse indexResponse = client.index(source,RequestOptions.DEFAULT);
		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());		//对应命令返回的状态 CREATED

	}

	//5 获取文档，判断是否存在 get enron_index/doc/1
    @Test
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

		System.out.println(getResponse.getSourceAsString());	//打印文档的内容

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
//		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("2","name age");

		//QueryBuilders.matchAllQuery匹配所有
//		MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

		//分页
//		searchSourceBuilder.from();
//		searchSourceBuilder.size();
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)).query(termQueryBuilder).trackTotalHits(true);
		searchRequest.source(searchSourceBuilder);


		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		System.out.println(searchResponse.getHits().getTotalHits());
		System.out.println(JSON.toJSONString(searchResponse.getHits()));
		System.out.println("===================================");
		for (SearchHit documentFields: searchResponse.getHits().getHits()){
			System.out.println(documentFields.getSourceAsMap());
		}
	}


}
