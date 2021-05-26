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
