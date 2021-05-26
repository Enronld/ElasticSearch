package com.enron.controller;

import com.alibaba.fastjson.JSONObject;
import com.enron.pojo.Document;
import com.enron.service.ElasticsearchOptionService;
import com.enron.utils.Excel;
import com.enron.utils.ExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin
public class ElasticsearchOptionController {

    @Autowired
    private ElasticsearchOptionService elasticsearchOptionService;

    //显示所有索引
    @GetMapping("/showAllIndices")
    public String showAllIndices() {
        JSONObject result = new JSONObject();
        try {
            result.put("status","success");
            result.put("detail",elasticsearchOptionService.getAllIndices());
        } catch (Exception e) {
            String type = new ExceptionType().getExceptionType(e);
            if (type.equals("index_not_found_exception")){
                result.put("status","failed");
                result.put("detail","没有索引");
            }else{
                result.put("status","failed");
                result.put("detail","io异常");
            }
//            e.printStackTrace();
        }
        return result.toJSONString();
    }

    //创建索引
    @GetMapping("/createIndex/{index}")
    public String createIndex(@PathVariable("index") String index) {
        JSONObject result = new JSONObject();
        try {
            if(elasticsearchOptionService.createIndex(index)){
                result.put("status","success");
                result.put("detail","创建索引成功");
            }else{
                result.put("status","success");
                result.put("detail","创建索引失败");
            }
        } catch (Exception e) {
            String type = new ExceptionType().getExceptionType(e);
            if (type.equals("resource_already_exists_exception")){
                result.put("status","failed");
                result.put("detail","该索引已经存在");
            }else{
                result.put("status","failed");
                result.put("detail","io异常");
            }
        }
        return result.toJSONString();
    }

    //删除索引
    @GetMapping("/deleteIndex/{index}")
    public String deleteIndex(@PathVariable("index") String index) {
        JSONObject result = new JSONObject();
        try {
            if(elasticsearchOptionService.deleteIndex(index)){
                result.put("status","failed");
                result.put("detail","删除索引失败");
            }else{
                result.put("status","success");
                result.put("detail","删除索引成功");
            }
        } catch (IOException e) {
            result.put("status","failed");
            result.put("detail","io异常");
        }
        return result.toJSONString();
    }

    //添加文档
    @GetMapping("/addDocument/{index}/{id}/{key}/{value}")
    public String deleteIndex(@PathVariable("index") String index, @PathVariable("id") String id, @PathVariable("key") List<String> key, @PathVariable("value") List<String> value) {
        JSONObject result = new JSONObject();
        try {
            System.out.println("-----------------------" +elasticsearchOptionService.addDocument(index,id,key,value));
            if (elasticsearchOptionService.addDocument(index,id,key,value).equals("OK")){
                result.put("status","success");
                result.put("detail","数据添加成功");
            } else{
                result.put("status","failed");
                result.put("detail","数据添加失败");
            }
        } catch (IOException e) {
            result.put("status","failed");
            result.put("detail","io异常");
        }
        return result.toJSONString();
    }

    //获取文档数量
    @GetMapping("/getDocumentNum")
    public String getDocumentNum() {
        JSONObject result = new JSONObject();
        long num = elasticsearchOptionService.getSearchDocumentNum();
        if (num != 0){
            result.put("status","success");
            result.put("detail",num);
        } else{
            result.put("status","failed");
            result.put("detail","暂无文档信息");
        }
        return result.toJSONString();
    }

    //获取文档信息
    @GetMapping("/getDocument/{index}/{search}/{field}/{pageNo}/{pageSize}")
    public String getDocument(@PathVariable("index") String index,@PathVariable("search") String search,@PathVariable("field") String field,@PathVariable("pageNo") Integer pageNo,@PathVariable("pageSize") Integer pageSize) {
        JSONObject result = new JSONObject();
        try {
//            System.out.println(elasticsearchOptionService.getDocument(index));
            List<Document> documents = new ArrayList<>();
            if (search.trim().isEmpty()){
                documents = elasticsearchOptionService.getDocument(index,0,search,field,pageNo,pageSize);       //所有文档
            }else{
                documents = elasticsearchOptionService.getDocument(index,1,search.trim(),field,pageNo,pageSize);    //多条件匹配
            }

            if (documents.size() != 0){
                result.put("status","success");
                result.put("detail",documents);
            } else{
                result.put("status","failed");
                result.put("detail","暂无文档信息");
            }
        } catch (IOException e) {
            result.put("status","failed");
            result.put("detail","io异常");
        }
        return result.toJSONString();
    }


    //更新文档信息
    @GetMapping("/updateDocument/{index}/{id}/{key}/{value}")
    public String updateIndex(@PathVariable("index") String index, @PathVariable("id") String id, @PathVariable("key") List<String> key, @PathVariable("value") List<String> value) {
        JSONObject result = new JSONObject();
        try {
            System.out.println("---------------------------------------------------");
            System.out.println(elasticsearchOptionService.updateDocument(index,id,key,value));
            if (elasticsearchOptionService.updateDocument(index,id,key,value).equals("OK")){
                result.put("status","success");
                result.put("detail","数据修改成功");
            } else{
                result.put("status","failed");
                result.put("detail","数据修改失败");
            }
        } catch (IOException e) {
            result.put("status","failed");
            result.put("detail","io异常");
        }
        return result.toJSONString();
    }

    //删除文档
    @GetMapping("/deleteDocument/{index}/{id}")
    public String deleteIndex(@PathVariable("index") String index,@PathVariable("id") String id) {
        JSONObject result = new JSONObject();
        try {
            if(elasticsearchOptionService.deleteDocument(index,id)){
                result.put("status","failed");
                result.put("detail","删除文档失败");
            }else{
                result.put("status","success");
                result.put("detail","删除文档成功");
            }
        } catch (IOException e) {
            result.put("status","failed");
            result.put("detail","io异常");
        }
        return result.toJSONString();
    }

    //批量导入文档
    @RequestMapping(value = "uploadDocument", method = RequestMethod.POST)
    public String uploadDocument(MultipartFile file) {
        JSONObject result = new JSONObject();
        try {
            File upload = Excel.multipartFileToFile(file);
            try {
                if(elasticsearchOptionService.uploadDocument(upload)){
                    result.put("status","failed");
                    result.put("detail","批量导入失败");
                }else{
                    result.put("status","success");
                    result.put("detail","批量导入成功");
                }
            } catch (IOException e) {
                result.put("status","failed");
                result.put("detail","io异常");
            }
        } catch (Exception e) {
            result.put("status","failed");
            result.put("detail","文件处理异常");
            e.printStackTrace();
        }

        return result.toJSONString();
    }



}
