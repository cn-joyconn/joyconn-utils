package cn.joyconn.utils.netutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;

import cn.joyconn.utils.loghelper.LogHelper;

/**
 * Created by Eric.Zhang on 2017/1/11.
 */
public class HttpRequestUtil {



    //region 数据成员
    /// <summary>PC（包括iE、google、sarfri）
    /// </summary>
    static  String[] PC = { "Windows NT", "Macintosh" };
    /// <summary>安卓
    /// </summary>
    static  String[] Android = { "Android" };
    /// <summary>IOS（"iPhone", "iPod", "iPad" ）
    /// </summary>
    static  String[] IOS = { "iPhone", "iPod", "iPad" };
    /// <summary>windows phone
    /// </summary>
    static  String[] WP = { "Windows Phone" };
    /// <summary>微信
    /// </summary>
    static  String[] WX = { "micromessenger","wechat","miniprogram" };
    /// <summary>微信浏览器
    /// </summary>
    static  String[] WXBrowswr = { "micromessenger","wechat" };
    /// <summary>微信小程序
    /// </summary>
    static  String[] WXMiniApp = { "miniprogram" };

    /// <summary>支付宝
    /// </summary>
    static  String[] alipayclient = { "alipayclient"};
    //endregion


    /**
     * 获取客户端类型----pc还是移动端
     * @param request
     * @return 1:pc/未知   ， 0:mobile
     */
    public static int getClientType(HttpServletRequest request)
    {
        String userAgent = request.getHeader( "USER-AGENT" ).toLowerCase();
        if (userAgent != null)
        {
            List<String> pcList = new ArrayList<>();
            for(String s : PC){
                pcList.add(s.toLowerCase());
            }
            List<String> moblieList = new ArrayList<>();
            for(String s : Android){
                moblieList.add(s.toLowerCase());
            }
            for(String s : IOS){
                moblieList.add(s.toLowerCase());
            }
            for(String s : WP){
                moblieList.add(s.toLowerCase());
            }
            for(String s : WP){
                moblieList.add(s.toLowerCase());
            }
            for(String s : alipayclient){
                moblieList.add(s.toLowerCase());
            }
            if (pcList.parallelStream().anyMatch(source -> userAgent.indexOf(source) > -1))//判断是否是pc端
            {
                return 1;
            }
            else if ( moblieList.parallelStream().anyMatch(ua -> userAgent.indexOf(ua) > -1))//判断是否是移动端。
            {
                return 0;
            }

        }
        return 1;
    }

    /**
     * 获取客户端Ip
     * @param request
     */
    public static String getClientIp(HttpServletRequest request){
        String ips = request.getHeader("X-Forwarded-For");
        if(ips!=null&&!"".equals(ips)){
            String[] ipArr = ips.split(",");
            return ipArr[0];
        }else{
            return  request.getRemoteAddr();
        }
    }
    /**
     * 是否利用cookie
     * @param request
     * @return 1:利用   ， 0:不利用
     */
    public static int noCookie(HttpServletRequest request)
    {
        String userAgent = request.getHeader( "USER-AGENT" ).toLowerCase();
        if (userAgent != null)
        {
            List<String> moblieList = new ArrayList<>();
            for(String s : WX){
                moblieList.add(s.toLowerCase());
            } if ( moblieList.parallelStream().anyMatch(ua -> userAgent.indexOf(ua) > -1))
            {
                return 0;
            }

        }
        return 1;
    }

    private static String paramToQueryString( Map<String, String> queryParams) throws Exception{
        String result ="";
        if (queryParams != null) {
            List<NameValuePair> queryPairs=new ArrayList<>(queryParams.size());
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                queryPairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
            }
            if(queryPairs.size()>0){
                result = EntityUtils.toString(new UrlEncodedFormEntity(queryPairs));
            }
        }
        return  result;
    }

    //region 同步请求

    //region get
    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     *
     * @param url                 请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url, String queryString) {
        String response = null;
        CloseableHttpClient client  = HttpClients.createDefault();
        return doGet(url,queryString,client);
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     *
     * @param url                 请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url, String queryString,RequestConfig reqConfig) {
        String response = null;
        CloseableHttpClient client  = HttpClients.createDefault();
        return doGet(url,queryString,reqConfig,client);
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * @param url   请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param client  HttpClient实现类的实例对象
     * @return
     */
    public static String doGet(String url, String queryString, CloseableHttpClient client) {
        HttpResponseModel responseModel = doGet(url,queryString,null,null,client);
        if(responseModel!=null&&responseModel.getCode()==HttpStatus.SC_OK){
            return responseModel.getContent();
        }
        return "";
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * @param url   请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param client  HttpClient实现类的实例对象
     * @return
     */
    public static String doGet(String url, String queryString,RequestConfig reqConfig, CloseableHttpClient client) {
        HttpResponseModel responseModel = doGet(url,queryString,null,reqConfig,client);
        if(responseModel!=null&&responseModel.getCode()==HttpStatus.SC_OK){
            return responseModel.getContent();
        }
        return "";
    }
    /**
     * 执行一个HTTP Get请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param params 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doGet(String url, Map<String, String> params,Map<String, String> headers) {
        CloseableHttpClient client  = HttpClients.createDefault();
        return doGet(url,params,headers,client);
    }
    /**
     * 执行一个HTTP Get请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param params 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doGet(String url, Map<String, String> params,Map<String, String> headers,RequestConfig reqConfig) {
        CloseableHttpClient client  = HttpClients.createDefault();
        return doGet(url,params,headers,reqConfig,client);
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * @param url 请求的URL地址
     * @param queryParams 请求的查询参数,可以为null
     * @param headers http请求头中的参数
     * @param client  HttpClient实现类的实例对象
     * @return
     */
    public static HttpResponseModel doGet(String url, Map<String, String> queryParams, Map<String, String> headers, CloseableHttpClient client) {


        return doGet(url,queryParams,headers,null,client);
    }
    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * @param url 请求的URL地址
     * @param queryParams 请求的查询参数,可以为null
     * @param headers http请求头中的参数
     * @param client  HttpClient实现类的实例对象
     * @return
     */
    public static HttpResponseModel doGet(String url, Map<String, String> queryParams, Map<String, String> headers, RequestConfig reqConfig,CloseableHttpClient client) {
        String queryString = "";
        if (queryParams != null) {
            try {
                queryString = paramToQueryString(queryParams);
                if(  queryString!=null && !"".equals(queryParams)){
                    if(url.indexOf("?")>0 ){
                        url +="&" + queryString;
                    }else{
                        url +="?" + queryString;
                    }
                }
            }catch (Exception ex){
                LogHelper.logger().error("执行HTTP Get" + url+ "时，序列化参数失败：", ex.getMessage());
            }
        }

        return doGet(url,queryString,headers,reqConfig,client);
    }

    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * @param url 请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param headers http请求头中的参数
     * @param client  HttpClient实现类的实例对象
     * @return
     */
    public static HttpResponseModel doGet(String url, String queryString,Map<String, String> headers, CloseableHttpClient client) {
        return doGet(url,queryString,headers,null,client);
    }
    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * @param url 请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param headers http请求头中的参数
     * @param client  HttpClient实现类的实例对象
     * @return
     */
    public static HttpResponseModel doGet(String url, String queryString,Map<String, String> headers,RequestConfig reqConfig, CloseableHttpClient client) {
        HttpGet httpGet = getHttpGet(url, queryString, headers, reqConfig);
        return doRequest(client,httpGet);
    }

    //endregion

    //region post

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers) {
        CloseableHttpClient client  = HttpClients.createDefault();
        return doPost(url,null,bodyParams,headers,null,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers, RequestConfig reqConfig) {
        CloseableHttpClient client  = HttpClients.createDefault();
        return doPost(url,null,bodyParams,headers,reqConfig,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> bodyParams,Map<String, String> headers) {
        return doPost(url,null,bodyParams,headers,null);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> bodyParams,Map<String, String> headers, RequestConfig reqConfig) {
        return doPost(url,null,bodyParams,headers,reqConfig);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     * @param url 请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers http请求头中的参数
     * @return
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams,Map<String, String> bodyParams,Map<String, String> headers) {

        return doPost(url,queryParams,bodyParams,headers,null);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     * @param url 请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers http请求头中的参数
     * @return
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams,Map<String, String> bodyParams,Map<String, String> headers, RequestConfig reqConfig) {

        CloseableHttpClient client  = HttpClients.createDefault();
        List<NameValuePair> nameValuePairs = null;
        if(bodyParams!=null&&bodyParams.size()>0){
            nameValuePairs = new ArrayList<>(bodyParams.size() );
            for(String key : bodyParams.keySet()){
                nameValuePairs.add(new BasicNameValuePair(key,bodyParams.get(key)));
            }

        }
        return doPost(url,queryParams,nameValuePairs,headers,reqConfig,client);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param entity  请求的实体信息,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, HttpEntity entity, Map<String, String> headers) {
        return doPost(url,entity,headers,null);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param entity  请求的实体信息,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, HttpEntity entity, Map<String, String> headers, RequestConfig reqConfig) {
        return doPost(url,null,entity,headers,reqConfig);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param entity  请求的实体信息,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url,Map<String, String> queryParams,  HttpEntity entity, Map<String, String> headers) {
        return doPost(url,queryParams,entity,headers,null);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param entity  请求的实体信息,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url,Map<String, String> queryParams,  HttpEntity entity, Map<String, String> headers, RequestConfig reqConfig) {
        CloseableHttpClient client  = HttpClients.createDefault();        
        HttpPost httpPost = getHttpPost(url, queryParams, entity, headers, reqConfig);
        return doRequest(client,httpPost);
    }



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers,CloseableHttpClient client) {
        return doPost(url,bodyParams,headers,null,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers, RequestConfig reqConfig,CloseableHttpClient client) {
        return doPost(url,null,bodyParams,headers,reqConfig,client);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,CloseableHttpClient client) {
        return doPost(url,queryParams,bodyParams,headers,null,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers, RequestConfig reqConfig,CloseableHttpClient client) {
        return doPost(url,queryParams,bodyParams,headers,null,null,reqConfig,client);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param fileName  上传的文件名
     * @param fileBytes  上传的文件流
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes) {
        return doPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,null);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param fileName  上传的文件名
     * @param fileBytes  上传的文件流
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig) {
        return doPost(url,queryParams,bodyParams,headers,fileName,fileBytes,reqConfig,null);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param fileName  上传的文件名
     * @param fileBytes  上传的文件流
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes,CloseableHttpClient client) {
        return doPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param fileName  上传的文件名
     * @param fileBytes  上传的文件流
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig,CloseableHttpClient client) {
        List<NameValuePair> nameValuePairs = null;
        if(bodyParams!=null&&bodyParams.size()>0){
            nameValuePairs = new ArrayList<>(bodyParams.size() );
            for(String key : bodyParams.keySet()){
                nameValuePairs.add(new BasicNameValuePair(key,bodyParams.get(key)));
            }

        }
        return doPost(url,queryParams,nameValuePairs,headers,fileName,fileBytes,reqConfig,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param fileName  上传的文件名
     * @param fileBytes  上传的文件流
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes,CloseableHttpClient client) {
        return doPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,client);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams 请求的form参数,可以为null
     * @param headers 请求的header,可以为null
     * @param fileName  上传的文件名
     * @param fileBytes  上传的文件流
     * @param client  HttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig,CloseableHttpClient client) {
        if(client==null){
            client  = HttpClients.createDefault();
        }
        HttpPost httpPost = getHttpPost(url,queryParams,bodyParams,headers,fileName,fileBytes,reqConfig);
        return doRequest(client,httpPost);
    }


    //endregion


    private static HttpResponseModel doRequest(CloseableHttpClient httpClient,HttpRequestBase requestBase){
        HttpResponseModel result=new HttpResponseModel();
        //CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try{

//			httpPut.setConfig(config);
            response = httpClient.execute(requestBase);
            result.code=response.getStatusLine().getStatusCode();
            result.content = EntityUtils.toString(response.getEntity(), "UTF-8");
        }catch(Exception e){
            LogHelper.logger().error("执行HTTP "+requestBase.getMethod()+" " + requestBase.getURI().toString()+ "时，发生异常！请求参数：", e.getMessage());
        }finally{
            try{
                if(response != null){
                    response.close();
                }
                if(httpClient != null) {
                    httpClient.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    //endregion

    //region 异步请求

    //     //region get
    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  *
    //  * @param url                 请求的URL地址
    //  * @param queryString 请求的查询参数,可以为null
    //  * @return 返回请求响应的HTML
    //  */
    // public static void doAsyncGet(String url, String queryString,Consumer<String> callback) {
    //     CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //     doAsyncGet(url,queryString,client,callback);
    // }

    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  *
    //  * @param url                 请求的URL地址
    //  * @param queryString 请求的查询参数,可以为null
    //  * @return 返回请求响应的HTML
    //  */
    // public static void doAsyncGet(String url, String queryString,RequestConfig reqConfig,Consumer<String> callback) {
    //     CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //     doAsyncGet(url,queryString,reqConfig,client,(responseModel)->{
    //         if(responseModel!=null&&responseModel.getCode()==HttpStatus.SC_OK){
    //             callback.accept(responseModel.getContent());
    //         }
    //         callback.accept("");
    //     });
    // }

    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  * @param url   请求的URL地址
    //  * @param queryString 请求的查询参数,可以为null
    //  * @param client  HttpClient实现类的实例对象
    //  * @return
    //  */
    // public static void doAsyncGet(String url, String queryString, CloseableHttpAsyncClient client,Consumer<String> callback) {
    //     doAsyncGet(url,queryString,null,null,client,(responseModel)->{
    //         if(responseModel!=null&&responseModel.getCode()==HttpStatus.SC_OK){
    //             callback.accept(responseModel.getContent());
    //         }
    //         callback.accept("");
    //     });
    // }

    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  * @param url   请求的URL地址
    //  * @param queryString 请求的查询参数,可以为null
    //  * @param client  HttpClient实现类的实例对象
    //  * @return
    //  */
    // public static void doAsyncGet(String url, String queryString,RequestConfig reqConfig, CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //     doAsyncGet(url,queryString,null,reqConfig,client,callback);        
    // }
    // /**
    //  * 执行一个HTTP Get请求，返回请求响应的内容
    //  *
    //  * @param url        请求的URL地址
    //  * @param params 请求的参数,可以为null
    //  * @param headers 请求的header,可以为null
    //  * @return 返回请求响应
    //  */
    // public static void doAsyncGet(String url, Map<String, String> params,Map<String, String> headers,Consumer<HttpResponseModel> callback) {
    //     CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //     doAsyncGet(url,params,headers,client,callback);
    // }
    // /**
    //  * 执行一个HTTP Get请求，返回请求响应的内容
    //  *
    //  * @param url        请求的URL地址
    //  * @param params 请求的参数,可以为null
    //  * @param headers 请求的header,可以为null
    //  * @return 返回请求响应
    //  */
    // public static void doAsyncGet(String url, Map<String, String> params,Map<String, String> headers,RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {
    //     CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //     doAsyncGet(url,params,headers,reqConfig,client,callback);
    // }

    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  * @param url 请求的URL地址
    //  * @param queryParams 请求的查询参数,可以为null
    //  * @param headers http请求头中的参数
    //  * @param client  HttpClient实现类的实例对象
    //  * @return
    //  */
    // public static void doAsyncGet(String url, Map<String, String> queryParams, Map<String, String> headers, CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //     doAsyncGet(url,queryParams,headers,null,client,callback);
    // }
    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  * @param url 请求的URL地址
    //  * @param queryParams 请求的查询参数,可以为null
    //  * @param headers http请求头中的参数
    //  * @param client  HttpClient实现类的实例对象
    //  * @return
    //  */
    // public static void doAsyncGet(String url, Map<String, String> queryParams, Map<String, String> headers, RequestConfig reqConfig,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //     String queryString = "";
    //     if (queryParams != null) {
    //         try {
    //             queryString = paramToQueryString(queryParams);
    //             if( Strings.isNotBlank(queryString)){
    //                 if(url.indexOf("?")>0 ){
    //                     url +="&" + queryString;
    //                 }else{
    //                     url +="?" + queryString;
    //                 }
    //             }
    //         }catch (Exception ex){
    //             LogHelper.logger().error("执行HTTP Get" + url+ "时，序列化参数失败：", ex.getMessage());
    //         }
    //     }

    //     doAsyncGet(url,queryString,headers,reqConfig,client,callback);
    // }

    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  * @param url 请求的URL地址
    //  * @param queryString 请求的查询参数,可以为null
    //  * @param headers http请求头中的参数
    //  * @param client  HttpClient实现类的实例对象
    //  * @return
    //  */
    // public static void doAsyncGet(String url, String queryString,Map<String, String> headers, CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //     doAsyncGet(url,queryString,headers,null,client,callback);
    // }
    // /**
    //  * 执行一个HTTP GET请求，返回请求响应的内容
    //  * @param url 请求的URL地址
    //  * @param queryString 请求的查询参数,可以为null
    //  * @param headers http请求头中的参数
    //  * @param client  HttpClient实现类的实例对象
    //  * @return
    //  */
    // public static void doAsyncGet(String url, String queryString,Map<String, String> headers,RequestConfig reqConfig, CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //     HttpGet httpGet = getHttpGet(url, queryString, headers, reqConfig);
    //     doAsyncRequest(client,httpGet,callback);
    // }

    // //endregion

    //     //region post

    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param bodyParams 请求的参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers,Consumer<HttpResponseModel> callback) {
    //         CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //         doAsyncPost(url,null,bodyParams,headers,null,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param bodyParams 请求的参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers, RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {
    //         CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //         doAsyncPost(url,null,bodyParams,headers,reqConfig,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param bodyParams 请求的参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> bodyParams,Map<String, String> headers,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,null,bodyParams,headers,null,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param bodyParams 请求的参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> bodyParams,Map<String, String> headers, RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,null,bodyParams,headers,reqConfig,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      * @param url 请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers http请求头中的参数
    //      * @return
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams,Map<String, String> bodyParams,Map<String, String> headers,Consumer<HttpResponseModel> callback) {

    //         doAsyncPost(url,queryParams,bodyParams,headers,null,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      * @param url 请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers http请求头中的参数
    //      * @return
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams,Map<String, String> bodyParams,Map<String, String> headers, RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {

    //         CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //         List<NameValuePair> nameValuePairs = null;
    //         if(bodyParams!=null&&bodyParams.size()>0){
    //             nameValuePairs = new ArrayList<>(bodyParams.size() );
    //             for(String key : bodyParams.keySet()){
    //                 nameValuePairs.add(new BasicNameValuePair(key,bodyParams.get(key)));
    //             }

    //         }
    //         doAsyncPost(url,queryParams,nameValuePairs,headers,reqConfig,client,callback);
    //     }

    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param entity  请求的实体信息,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, HttpEntity entity, Map<String, String> headers,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,entity,headers,null,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param entity  请求的实体信息,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, HttpEntity entity, Map<String, String> headers, RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,null,entity,headers,reqConfig,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param entity  请求的实体信息,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url,Map<String, String> queryParams,  HttpEntity entity, Map<String, String> headers,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,entity,headers,null,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param entity  请求的实体信息,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url,Map<String, String> queryParams,  HttpEntity entity, Map<String, String> headers, RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {
    //         CloseableHttpAsyncClient client  = HttpAsyncClients.createDefault();
    //         HttpPost httpPost = getHttpPost(url, queryParams, entity, headers, reqConfig);
    //         doAsyncRequest(client,httpPost,callback);
    //     }



    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,bodyParams,headers,null,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers, RequestConfig reqConfig,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,null,bodyParams,headers,reqConfig,client,callback);
    //     }

    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,bodyParams,headers,null,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers, RequestConfig reqConfig,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,bodyParams,headers,null,null,reqConfig,client,callback);
    //     }

    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param fileName  上传的文件名
    //      * @param fileBytes  上传的文件流
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,null,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param fileName  上传的文件名
    //      * @param fileBytes  上传的文件流
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,null,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param fileName  上传的文件名
    //      * @param fileBytes  上传的文件流
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param fileName  上传的文件名
    //      * @param fileBytes  上传的文件流
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         List<NameValuePair> nameValuePairs = null;
    //         if(bodyParams!=null&&bodyParams.size()>0){
    //             nameValuePairs = new ArrayList<>(bodyParams.size() );
    //             for(String key : bodyParams.keySet()){
    //                 nameValuePairs.add(new BasicNameValuePair(key,bodyParams.get(key)));
    //             }

    //         }
    //         doAsyncPost(url,queryParams,nameValuePairs,headers,fileName,fileBytes,null,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param fileName  上传的文件名
    //      * @param fileBytes  上传的文件流
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         doAsyncPost(url,queryParams,bodyParams,headers,fileName,fileBytes,null,client,callback);
    //     }
    //     /**
    //      * 执行一个HTTP POST请求，返回请求响应的内容
    //      *
    //      * @param url        请求的URL地址
    //      * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
    //      * @param bodyParams 请求的form参数,可以为null
    //      * @param headers 请求的header,可以为null
    //      * @param fileName  上传的文件名
    //      * @param fileBytes  上传的文件流
    //      * @param client  HttpClient实现类的实例对象
    //      * @return 返回请求响应
    //      */
    //     public static void doAsyncPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig,CloseableHttpAsyncClient client,Consumer<HttpResponseModel> callback) {
    //         if(client==null){
    //             client  =  HttpAsyncClients.createDefault();
    //         }
    //         HttpPost httpPost = getHttpPost(url,queryParams,bodyParams,headers,fileName,fileBytes,reqConfig);
    //         doAsyncRequest(client,httpPost,callback);
    //     }

    //     //endregion



    //     private static void doAsyncRequest(CloseableHttpAsyncClient httpAsyncClient,HttpRequestBase requestBase,Consumer<HttpResponseModel> callback){
    //         // final CountDownLatch latch = new CountDownLatch(1);
    //         httpAsyncClient.start();
    //         httpAsyncClient.execute(requestBase, new FutureCallback<HttpResponse>() {            
    //             public void completed(final HttpResponse response) {
    //                 try{
    //                     closeHttpAsyncClient(httpAsyncClient);
    //                     HttpResponseModel result=new HttpResponseModel();
    //                     result.code=response.getStatusLine().getStatusCode();
    //                     result.content = EntityUtils.toString(response.getEntity(), "UTF-8");
    //                     callback.accept(result);
    //                 }catch(Exception e){
    //                     LogHelper.logger().error("执行HTTP "+requestBase.getMethod()+" " + requestBase.getURI().toString()+ "时，读取结果失败："+ e.getMessage());
    //                 }
    //             }

    //             public void failed(final Exception ex) {      
    //                 closeHttpAsyncClient(httpAsyncClient);
    //                 LogHelper.logger().error("执行HTTP "+requestBase.getMethod()+" " + requestBase.getURI().toString()+ "时，发生异常！异常信息："+ex.getLocalizedMessage());
    //             }
    //             public void cancelled() {
    //                 closeHttpAsyncClient(httpAsyncClient);
    //             }

    //         }); 
    //     }
    //     private static void closeHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient){
    //         try {
    //             httpAsyncClient.close();
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
        
    //endregion


    //region private
    private static HttpGet getHttpGet(String url, String queryString,Map<String, String> headers,RequestConfig reqConfig){
        HttpGet httpGet = new HttpGet(url);
        if(queryString==null){
            queryString="";
        }
        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        try{
            httpGet.setURI(new URIBuilder(httpGet.getURI().toString() + queryString).build());
        }catch (URISyntaxException e) {
            LogHelper.logger().error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
        }
        if(reqConfig==null){
            reqConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(10000) // 设置连接超时时间
                    .setSocketTimeout(10000) // 设置读取超时时间
                    .setExpectContinueEnabled(false)
                    .setCircularRedirectsAllowed(true) // 允许多次重定向
                    .build();
        }
        httpGet.setConfig(reqConfig);
        return httpGet;
    }
 
    private static HttpPost getHttpPost(String url,Map<String, String> queryParams,  HttpEntity entity, Map<String, String> headers, RequestConfig reqConfig){
        HttpPost httpPost = new HttpPost(url);
        String queryString ="";
        try {
            queryString = paramToQueryString(queryParams);
            if( Strings.isNotBlank(queryString) ){
                if(url.indexOf("?")>0 ){
                    url +="&" + queryString;
                }else{
                    url +="?" + queryString;
                }
            }
        }catch (Exception ex){
            LogHelper.logger().error("执行HTTP Post" + url+ "时，序列化queryParams参数失败：", ex.getMessage());
        }

        if (entity != null) {
            httpPost.setEntity(entity);
        }
        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if(reqConfig==null){
            reqConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(10000) // 设置连接超时时间
                    .setSocketTimeout(10000) // 设置读取超时时间
                    .setExpectContinueEnabled(false)
                    .setCircularRedirectsAllowed(true) // 允许多次重定向
                    .build();
        }
        httpPost.setConfig(reqConfig);
        return httpPost;
    }
 
    private static HttpPost getHttpPost(String url, Map<String, String> queryParams, List<NameValuePair> bodyParams,Map<String, String> headers,String fileName,byte [] fileBytes, RequestConfig reqConfig){
        HttpPost httpPost = new HttpPost(url);
        //设置Http Post数据
        String queryString ="";
        try {
            queryString = paramToQueryString(queryParams);
            if( Strings.isNotBlank(queryString)  ){
                if(url.indexOf("?")>0 ){
                    url +="&" + queryString;
                }else{
                    url +="?" + queryString;
                }
            }
        }catch (Exception ex){
            LogHelper.logger().error("执行HTTP Post" + url+ "时，序列化queryParams参数失败：", ex.getMessage());
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.RFC6532);
        if(fileBytes!=null && fileBytes.length>0){
            InputStream inputStream = new ByteArrayInputStream(fileBytes);
            builder.addBinaryBody(fileName, inputStream, ContentType.create("multipart/form-data"), fileName);
        }
        if (bodyParams != null&&bodyParams.size()>0) {
            for(NameValuePair nameValuePair : bodyParams){
                StringBody stringBody = new StringBody(nameValuePair.getValue(),ContentType.MULTIPART_FORM_DATA);
                builder.addPart(nameValuePair.getName(),stringBody);
            }
        }
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        

        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        if(reqConfig==null){
            reqConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(10000) // 设置连接超时时间
                    .setSocketTimeout(10000) // 设置读取超时时间
                    .setExpectContinueEnabled(false)
                    .setCircularRedirectsAllowed(true) // 允许多次重定向
                    .build();
        }
        httpPost.setConfig(reqConfig);
        return httpPost;
    }
   
    //endregion
}

