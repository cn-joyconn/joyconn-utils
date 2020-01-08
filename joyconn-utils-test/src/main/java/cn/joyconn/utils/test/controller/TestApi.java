package cn.joyconn.utils.test.controller;

import cn.joyconn.utils.elasticsearch.highclient.RestHighLevelClientService;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("testapi")
public class TestApi {
    @Autowired
    RestHighLevelClientService restHighLevelClientService;

    @RequestMapping("test")
    String test(HttpServletResponse httpServletResponse) throws IOException {

//        RestClientAutoConfiguration.RestHighLevelClientConfiguration ss =new org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration.RestHighLevelClientConfiguration();
        String source = "{" +
                "\"title\" : \"耐苦无领运动半袖新AAA\"," +
                "\"price\" : 300," +
                "\"num\" : 800," +
                "\"date\" : \"2019-07-28\"" +
                "}";

//        IndexResponse response = restHighLevelClientService.addDoc("idx_shuihu", source);
        StringBuffer sb =new StringBuffer();
//        sb.append(response.toString());
        sb.append("\n");
        SearchResponse searchResponse = restHighLevelClientService.search("title","半袖",1,5);
        sb.append(searchResponse.toString());
        return sb.toString();
    }
}
