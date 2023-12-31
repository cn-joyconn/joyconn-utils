package cn.joyconn.utils.test.controller;

import cn.joyconn.utils.elasticsearch.highclient.RestHighLevelClientService;
import cn.joyconn.utils.encrypt.Base64Utils;
import cn.joyconn.utils.loghelper.LogHelper;
import cn.joyconn.utils.netutils.HttpRequestUtil;
import cn.joyconn.utils.netutils.OkHttp3ClientUtil;
import cn.joyconn.utils.test.testData.PointTestData;
import cn.joyconn.utils.uniqueID.TinyNextId;
import cn.joyconn.utils.uniqueID.idgenerator.IDGeneratorHelper;
import okhttp3.Response;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("testapi")
public class TestApi {
    @Autowired
    RestHighLevelClientService restHighLevelClientService;
    @Autowired
    IDGeneratorHelper idGeneratorHelper;

    @Autowired
    TinyNextId tinyNextId;
    @RequestMapping("test")
    String test(HttpServletResponse httpServletResponse) throws IOException {
        
        LogHelper.logger().info("idGeneratorHelper:"+ idGeneratorHelper.nextId());
        LogHelper.logger().info("idGeneratorHelper:"+ tinyNextId.nextId());
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
        // SearchResponse searchResponse = restHighLevelClientService.search("title","半袖",1,5);
        // sb.append(searchResponse.toString());

        // Map<String,String> loginparams =new HashMap<>();
        // loginparams.put("phone","18333660110");
        // loginparams.put("pwd","123456");
        // loginparams.put("remember","false");
        // Map<String, String> headers=new HashMap<>();
        // HttpRequestUtil.doAsyncPost("http://powermonitor.sdfy.joyconn.cn:10006/api/iotcomm/CommApi/AccountApi/dologinAuth", 
        //         null, loginparams, headers, (responseModel)->{
        //                 LogHelper.logger().info(responseModel.getContent());
        //     });
        // OkHttp3ClientUtil.doAsyncGet("http://www.baidu.com", 
        //     null, loginparams,  (response)->{
        //         String result = "";
        //         try {
        //             result = IOUtils.toString(response.body().byteStream(), "UTF-8");
        //         } catch (Exception e) {
        //             LogHelper.logger().error("执行HTTP Get 时，读取结果失败：", e.getMessage());
        //         }
        //         response.close();
        //         LogHelper.logger().info(result);
        // });
        // OkHttp3ClientUtil.doAsyncPost("http://powermonitor.sdfy.joyconn.cn:10006/api/iotcomm/CommApi/AccountApi/dologinAuth", 
        //     null, loginparams, null,null, (response)->{
        //         String result = "";
        //         try {
        //             result = IOUtils.toString(response.body().byteStream(), "UTF-8");
        //         } catch (Exception e) {
        //             LogHelper.logger().error("执行HTTP Get 时，读取结果失败：", e.getMessage());
        //         }
        //         response.close();
        //         LogHelper.logger().info(result);
        // });



            // ObjectMapper objectMapper = new ObjectMapper();
            // Map<String, String> params = new HashMap<>();
            // Map<String, String> header = new HashMap<>();
            // params.put("companycd", "SYS0001");
            // params.put("projectcd", "SYS00010000001");
            // params.put("roomcd", "SYS000100000010000001");
            // params.put("key", "123a");
            // String url = "updatePointValue";
            // try {
    
            //     url = "http://powermonitor.sdfy.joyconn.cn:10006/api/iotdata/FinalStationApi/FinalUploadByCompressDataApi/updatePointValue";
            //     // url = "http://localhost:20113/api/iotdata/FinalStationApi/FinalUploadByCompressDataApi/updatePointValue";
                
            //     byte[] bytes = Base64Utils.decode(PointTestData.getData());
            //     String printUrl=url;
            //     // HttpRequestUtil.doAsyncPost(url, null, params, header, "pointValuesStr", bytes,(responseModel)->{
            //     //     LogHelper.logger().info(responseModel.getContent());
            //     // });
            //     OkHttp3ClientUtil.doAsyncPost(url, null, params, header, "pointValuesStr", bytes,(response)->{
            //         String result = "";
            //         try {
            //             result = IOUtils.toString(response.body().byteStream(), "UTF-8");
            //         } catch (Exception e) {
            //             LogHelper.logger().error("执行HTTP Get" + printUrl + "时，读取结果失败：", e.getMessage());
            //         }
            //         response.close();
            //         LogHelper.logger().info(result);
            //     });
            // } catch (Exception ex) {
            //     LogHelper.logger("WebApiRequest").error("参数序列化失败:");
            // }
        return sb.toString();
    }
}
