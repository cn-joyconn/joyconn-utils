package top.mortise.utils.netutils;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */
import top.mortise.utils.loghelper.LogHelper;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import sun.net.www.http.*;


import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    public static int GetClientType(HttpServletRequest request)
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


    /**
     * 执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param url                 请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url, String queryString) {
        String response = null;
        HttpClient client = new HttpClient();
        return doGet(url,queryString,client);
    }
    public static String doGet(String url, String queryString, HttpClient client) {
        String response = null;
        HttpMethod method = new GetMethod(url);
        try {
            if (queryString!=null&& !queryString.equals(""))
                method.setQueryString(URIUtil.encodeQuery(queryString));
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                response = method.getResponseBodyAsString();
            }
        } catch (URIException e) {
            LogHelper.logger().error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
        } catch (IOException e) {
            LogHelper.logger().error("执行HTTP Get请求" + url + "时，发生异常！", e);
        } finally {
            method.releaseConnection();
        }
        return response;
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
        String response = null;
        HttpClient client = new HttpClient();
        return doGet(url,params,headers,client);
    }

    public static HttpResponseModel doGet(String url, Map<String, String> params, Map<String, String> headers, HttpClient client) {
        HttpMethod getMethod = new GetMethod(url);
        if (params != null) {
            NameValuePair [] queryPairs=new NameValuePair[params.size()];
            int i=0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                queryPairs[i]=new NameValuePair(entry.getKey(),entry.getValue());
                i++;

            }
            if(queryPairs.length>0){
                getMethod.setQueryString(queryPairs);
            }
        }
        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                getMethod.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }
        return doRequest(url,client,getMethod);
    }
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, List<NameValuePair> bodyParams,Map<String, String> headers) {
        HttpClient client = new HttpClient();
        return doPost(url,bodyParams,headers,client);
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
        HttpClient client = new HttpClient();
        //设置Http Post数据
        List<NameValuePair> dataPairs  =null;
        if (bodyParams != null) {
             dataPairs=new ArrayList<NameValuePair>();
            int i=0;
            for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                dataPairs.add(new NameValuePair(entry.getKey(),entry.getValue()));
                i++;
            }
        }
        return doPost(url,dataPairs,headers,client);
    }

    public static HttpResponseModel doPost(String url, Map<String, String> queryParams,Map<String, String> bodyParams,Map<String, String> headers) {
        HttpClient client = new HttpClient();
        for (String key : queryParams.keySet()) {
            if (url.indexOf("?") > 0) {
                url += "&";
            } else {
                url += "?";

            }
            url += key + "=" + queryParams.get(key);
        }

        return doPost(url,bodyParams,headers);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param entity 请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static HttpResponseModel doPost(String url, RequestEntity entity,Map<String, String> headers) {
        String response = null;
        HttpClient client = new HttpClient();
        PostMethod  postMethod = new PostMethod(url);
        HttpResponseModel result=new HttpResponseModel();
        if (entity != null) {

            postMethod.setRequestEntity(entity);

        }
        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                postMethod.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }


        return doRequest(url,client,postMethod);
    }
    public static HttpResponseModel doPost(String url, List<NameValuePair> params,Map<String, String> headers,HttpClient client) {
        String response = null;
        PostMethod  postMethod = new PostMethod(url);
        HttpResponseModel result=new HttpResponseModel();
        //设置Http Post数据

        if (params != null) {
            if(params.size()>0){
                NameValuePair[] valuePairs = new NameValuePair[params.size()];
                params.toArray(valuePairs);
                postMethod.setRequestBody(valuePairs);
            }
        }
        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                postMethod.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }


        return doRequest(url,client,postMethod);
    }
    public static HttpResponseModel doPost(String url, Map<String, String> queryParams, List<NameValuePair> params,Map<String, String> headers,HttpClient client) {
        String response = null;
        PostMethod  postMethod = new PostMethod(url);
        HttpResponseModel result=new HttpResponseModel();
        //设置Http Post数据
        for (String key : queryParams.keySet()) {
            if (url.indexOf("?") > 0) {
                url += "&";
            } else {
                url += "?";

            }
            url += key + "=" + queryParams.get(key);
        }
        if (params != null) {
            if(params.size()>0){
                NameValuePair[] valuePairs = new NameValuePair[params.size()];
                params.toArray(valuePairs);
                postMethod.setRequestBody(valuePairs);
            }
        }
        if(headers!=null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                postMethod.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }


        return doRequest(url,client,postMethod);
    }



    public static HttpResponseModel doRequest(String url, HttpClient client, HttpMethod  httpMethod) {
        HttpResponseModel result=new HttpResponseModel();
        try {
            client.executeMethod(httpMethod);
            result.code=httpMethod.getStatusCode();
            InputStream jsonStr;
            jsonStr = httpMethod.getResponseBodyAsStream();
            ByteArrayOutputStream baos   =   new   ByteArrayOutputStream();
            int   i=-1;
            while((i=jsonStr.read())!=-1){
                baos.write(i);
            }
            result.content = new String(baos.toByteArray(),"utf-8");
        }
        catch (IOException e) {
            LogHelper.logger().error("执行HTTP "+httpMethod.getName()+" " + url+ "时，发生异常！请求参数：", e.getMessage());
            result.code=404;
        }finally {
            httpMethod.releaseConnection();
        }

        return result;
    }


}

