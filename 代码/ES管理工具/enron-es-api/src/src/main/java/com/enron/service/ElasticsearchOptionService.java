package com.enron.service;

import com.alibaba.fastjson.JSON;
import com.enron.pojo.Document;
import com.enron.utils.Excel;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
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
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


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
                    .id(""+(i+1))						//不设置id有默认的随机id
                    .source(JSON.toJSONString(documents.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());	//是否失败 返回false代表成功
        return bulkResponse.hasFailures();
    }
}
