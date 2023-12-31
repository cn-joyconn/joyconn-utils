package cn.joyconn.utils.netutils;

import org.apache.commons.io.IOUtils;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */

import org.apache.logging.log4j.util.Strings;

import cn.joyconn.utils.loghelper.LogHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by Eric.Zhang on 2017/1/11.
 */
public class OkHttp3ClientUtil {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    // region 同步请求

    // region get
    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doSyncGet(String url, String queryString) {
        return doSyncGet(url, queryString, null);
    }




    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return
     */
    public static String doSyncGet(String url, String queryString,  OkHttpClient okHttpClient) {
        Response response = doSyncGet(url, queryString,null,  okHttpClient);
        String result = null;
        if (response != null && response.isSuccessful()) {
            try {
                result = IOUtils.toString(response.body().byteStream(), "UTF-8");
            } catch (Exception e) {
                LogHelper.logger().error("执行HTTP Get" + url + "时，读取结果失败：", e.getMessage());
            }
            response.close();
        }
        return result;
    }



    /**
     * 执行一个HTTP Get请求，返回请求响应的内容
     *
     * @param url     请求的URL地址
     * @param params  请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static Response doSyncGet(String url, Map<String, String> params, Map<String, String> headers) {
        return doSyncGet(url, params, headers,  null);
    }



    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数,可以为null
     * @param headers     http请求头中的参数
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return
     */
    public static Response doSyncGet(String url, Map<String, String> queryParams, Map<String, String> headers,OkHttpClient okHttpClient) {
        String queryString = "";
        if (queryParams != null) {
            try {
                queryString = setUrlParams(url,queryParams);
                
            } catch (Exception ex) {
                LogHelper.logger().error("执行HTTP Get" + url + "时，序列化参数失败：", ex.getMessage());
            }
        }

       return doSyncGet(url, queryString, headers,  okHttpClient);
    }



    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param headers     http请求头中的参数
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return
     */
    public static Response doSyncGet(String url, String queryString, Map<String, String> headers, OkHttpClient okHttpClient) {       
        String requestUrl = url;
        if(Strings.isNotBlank(queryString)){
            requestUrl+=( url.indexOf("?")>0?"?":"&" )+queryString;
        } 
        Request request = new Request.Builder()
                .url(requestUrl)
                .headers(setHeaders(headers))
                .get()
                .build();
    
        Response response =  doSyncRequest(okHttpClient, request);
        return response;
    }

    // endregion

    // region post

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers    请求的header,可以为null
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, Map<String, String> bodyParams, Map<String, String> headers) {
        return doSyncPost(url, null, bodyParams, headers);
    }




    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     http请求头中的参数
     * @return
     */
    public static Response doSyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers) {

        return doSyncPost(url, queryParams, bodyParams, headers, null);
    }



 

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url     请求的URL地址
     * @param requestBody  请求的实体信息,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, RequestBody requestBody, Map<String, String> headers) {
        return doSyncPost(url, null, requestBody, headers);
    }


    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param requestBody      请求的实体信息,可以为null
     * @param headers     请求的header,可以为null
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, Map<String, String> queryParams, RequestBody requestBody, Map<String, String> headers) {
        Request request = new Request.Builder()
        .url(url + setUrlParams(url ,queryParams))
        .headers(setHeaders(headers))
        .post(requestBody)
        .build();
        return doSyncRequest(null ,request);
    }



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的form参数,可以为null
     * @param headers    请求的header,可以为null
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, Map<String, String> bodyParams, Map<String, String> headers, OkHttpClient okHttpClient) {
        return doSyncPost(url, null, bodyParams, headers,  okHttpClient);
    }



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams, Map<String, String> headers,  OkHttpClient okHttpClient) {
        return doSyncPost(url, queryParams, bodyParams, headers, null, null,  okHttpClient);
    }



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param fileName    上传的文件名
     * @param fileBytes   上传的文件流
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,
            Map<String, String> headers, String fileName, byte[] fileBytes) {
        return doSyncPost(url, queryParams, bodyParams, headers, fileName, fileBytes,  null);
    }

   



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param fileName    上传的文件名
     * @param fileBytes   上传的文件流
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static Response doSyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,
            Map<String, String> headers, String fileName, byte[] fileBytes, OkHttpClient okHttpClient) {
                Request request = new Request.Builder()
                .url(url + setUrlParams(url ,queryParams))
                .headers(setHeaders(headers))
                .post(setPostMultipartBody(bodyParams,fileName,"",fileBytes))
                .build();
    
        return doSyncRequest(okHttpClient, request);
    }

    // endregion

    private static Response doSyncRequest(OkHttpClient okHttpClient,Request request) {
     
        if(okHttpClient==null){
            okHttpClient = client;            
        }
        Response result = null;
		try {
			result = okHttpClient.newCall(request).execute();
		} catch (IOException e) {
            LogHelper.logger().error("执行HTTP " + request.method() + " " + request.url().toString()
                        + "时，发生异常！异常信息：" + e.getLocalizedMessage());
		}
        return result;
    }

    // endregion

    // region 异步请求

    // region get



    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static void doAsyncGet(String url, String queryString,  Consumer<String> callback) {
        doAsyncGet(url, queryString,  null, (response) -> {
            if (response != null && response.isSuccessful()) {
                String result = "";
                try {
                    result = IOUtils.toString(response.body().byteStream(), "UTF-8");
                } catch (Exception e) {
                    LogHelper.logger().error("执行HTTP Get" + url + "时，读取结果失败：", e.getMessage());
                }
                response.close();
                callback.accept(result);
            }
            callback.accept(null);
        });
    }

  
    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return
     */
    public static void doAsyncGet(String url, String queryString,  OkHttpClient okHttpClient, Consumer<Response> callback) {
        doAsyncGet(url, queryString, null,  okHttpClient, callback);
    }

    

    /**
     * 执行一个HTTP Get请求，返回请求响应的内容
     *
     * @param url     请求的URL地址
     * @param params  请求的参数,可以为null
     * @param headers 请求的header,可以为null
     * @return 返回请求响应
     */
    public static void doAsyncGet(String url, Map<String, String> params, Map<String, String> headers,  Consumer<Response> callback) {
        doAsyncGet(url, params, headers,  null, callback);
    }



    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数,可以为null
     * @param headers     http请求头中的参数
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return
     */
    public static void doAsyncGet(String url, Map<String, String> queryParams, Map<String, String> headers,
            OkHttpClient okHttpClient, Consumer<Response> callback) {
        String queryString = "";
        if (queryParams != null) {
            try {
                queryString = setUrlParams(url,queryParams);
                
            } catch (Exception ex) {
                LogHelper.logger().error("执行HTTP Get" + url + "时，序列化参数失败：", ex.getMessage());
            }
        }

        doAsyncGet(url, queryString, headers,  okHttpClient, callback);
    }



    /**
     * 执行一个HTTP GET请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param headers     http请求头中的参数
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return
     */
    public static void doAsyncGet(String url, String queryString, Map<String, String> headers, OkHttpClient okHttpClient, Consumer<Response> callback) {
        String requestUrl = url;
        if(Strings.isNotBlank(queryString)){
            requestUrl+=( url.indexOf("?")>0?"?":"&" )+queryString;
        } 
        Request request = new Request.Builder()
                .url(requestUrl)
                .headers(setHeaders(headers))
                .get()
                .build();
    
        doAsyncRequest(okHttpClient, request, callback);
    }

    // endregion

    // region post


    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的参数,可以为null
     * @param headers    请求的header,可以为null
     * @return 返回请求响应
     */
    public static void doAsyncPost(String url, Map<String, String> bodyParams, Map<String, String> headers,Consumer<Response> callback) {
        doAsyncPost(url, null, bodyParams, headers,  callback);
    }

 
    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     * 
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     http请求头中的参数
     * @return
     */
    public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams,Map<String, String> headers,  Consumer<Response> callback) {     
        doAsyncPost(url, queryParams, bodyParams, headers, null, callback);
    }



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url     请求的URL地址
     * @param requestBody  请求的实体信息,可以为null
     * @param headers 请求的header,可以为null
     */
    public static void doAsyncPost(String url, RequestBody requestBody, Map<String, String> headers,Consumer<Response> callback) {
        doAsyncPost(url, null, requestBody, headers,  callback);
    }

  

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param requestBody      请求的实体信息,可以为null
     * @param headers     请求的header,可以为null
     */
    public static void doAsyncPost(String url, Map<String, String> queryParams, RequestBody requestBody,  Map<String, String> headers, Consumer<Response> callback) {
        Request request = new Request.Builder()
        .url(url + setUrlParams(url ,queryParams))
        .headers(setHeaders(headers))
        .post(requestBody)
        .build();

        doAsyncRequest(null, request, callback);
    }



    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url        请求的URL地址
     * @param bodyParams 请求的form参数,可以为null
     * @param headers    请求的header,可以为null
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static void doAsyncPost(String url, Map<String, String>  bodyParams, Map<String, String> headers,  OkHttpClient okHttpClient, Consumer<Response> callback) {
        doAsyncPost(url, null, bodyParams, headers,  okHttpClient, callback);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams, Map<String, String> headers,  OkHttpClient okHttpClient, Consumer<Response> callback) {
        doAsyncPost(url, queryParams, bodyParams, headers, null, null,  okHttpClient, callback);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param fileName    上传的文件名
     * @param fileBytes   上传的文件流
     * @return 返回请求响应
     */
    public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String> bodyParams, Map<String, String> headers, String fileName, byte[] fileBytes, Consumer<Response> callback) {
        doAsyncPost(url, queryParams, bodyParams, headers, fileName, fileBytes, null,  callback);
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param fileName    上传的文件名
     * @param fileBytes   上传的文件流
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String>  bodyParams, Map<String, String> headers, String fileName, byte[] fileBytes,OkHttpClient okHttpClient,Consumer<Response> callback) {
        doAsyncPost(url, queryParams, bodyParams, headers, fileName,"", fileBytes, okHttpClient,  callback);
    }
   /**
     * 执行一个HTTP POST请求，返回请求响应的内容
     *
     * @param url         请求的URL地址
     * @param queryParams 请求的查询参数（url中的queryString参数）,可以为null
     * @param bodyParams  请求的form参数,可以为null
     * @param headers     请求的header,可以为null
     * @param fileName    上传的文件名
     * @param fileBytes   上传的文件流
     * @param okHttpClient      OkHttpClient实现类的实例对象
     * @return 返回请求响应
     */
    public static void doAsyncPost(String url, Map<String, String> queryParams, Map<String, String>  bodyParams, Map<String, String> headers, String fileName,String mediaType,  byte[] fileBytes,OkHttpClient okHttpClient,Consumer<Response> callback) {
        Request request = new Request.Builder()
                .url(url + setUrlParams(url ,queryParams))
                .headers(setHeaders(headers))
                .post(setPostMultipartBody(bodyParams,fileName,mediaType,fileBytes))
                .build();
    
        doAsyncRequest(okHttpClient, request, callback);
    }
    // endregion

    private static void doAsyncRequest(OkHttpClient okHttpClient,Request request,  Consumer<Response> callback) {
        if(okHttpClient==null){
            okHttpClient = client;            
        }
        okHttpClient.newCall(request).enqueue(new Callback() {           
            public void onFailure(Call call, IOException e) {
                LogHelper.logger().error("执行HTTP " + request.method() + " " + request.url().toString()
                        + "时，发生异常！异常信息：" + e.getLocalizedMessage());
            }        
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    callback.accept(response);
                } catch (Exception e) {
                    LogHelper.logger().error("执行HTTP " + request.method() + " " + request.url().toString()
                            + "时，读取结果失败：" + e.getMessage());
                }
            }
        });
    }


    // endregion

    // region private
        /**
         * 设置get连接拼接参数
         * 
         * @return
         */
        private static String setUrlParams(String url,Map<String, String> queryParams)  {
            StringBuffer param = new StringBuffer();
            int i = 0;
            if (queryParams != null) {
                for (String key : queryParams.keySet()) {
                    if (i == 0)
                        param.append("?");
                    else
                        param.append("&");
                    try {
                        param.append(key).append("=").append(URLEncoder.encode(queryParams.get(key), "UTF-8")); // 字符串拼接
                    } catch (UnsupportedEncodingException e) {
                        LogHelper.logger().error("异步执行HTTP请求" + url+ "时，序列化queryParams参数失败：", e.getMessage());
                    }
                    i++;
                }
            }

            return param.toString();
        }

        /**
         * 设置Header头
         * 
         * @param headersParams
         * @return
         */
        private static Headers setHeaders(Map<String, String> headersParams) {
            Headers headers = null;

            Headers.Builder headerBuilder = new Headers.Builder();

            if (headersParams != null) {
                for (String key : headersParams.keySet()) {
                    headerBuilder.add(key, headersParams.get(key));
                }
            }

            headers = headerBuilder.build();

            return headers;
        }
       
        
        /**
         * 设置post Multipart/form-data请求
         * 
         * @param params
         * @return
         */
        private static RequestBody setPostMultipartBody(Map<String, String> params, String fileName,String mediaType, byte[] fileBytes) {

            RequestBody body = null;
            if(Strings.isNotBlank(fileName)&&fileBytes!=null){
                MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                MediaType fileType =Strings.isNotBlank(mediaType)?MediaType.parse(mediaType): MediaType.parse("multipart/form-data");//数据类型为json格式，
                if (params != null) {
                    for (String key : params.keySet()) {
                        formBodyBuilder.addFormDataPart(key, params.get(key));
                    }
                }    
                RequestBody fileBody = RequestBody.create(fileType,fileBytes);
                formBodyBuilder.addFormDataPart(fileName, fileName, fileBody);
                body = formBodyBuilder.build();
    
            }else{
                // MediaType fileType =Strings.isNotBlank(mediaType)?MediaType.parse(mediaType): MediaType.parse("application/x-www-form-urlencoded");//数据类型为json格式，
                // RequestBody requestBody = FormBody.create(MediaType.parse("application/json"),JSONObject.fromObject(params).toString());
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                if (params != null) {
                    for (String key : params.keySet()) {
                        formBodyBuilder.add(key, params.get(key));
                    }
                }
                body = formBodyBuilder.build();
            }
            
            return body;
        }
        
    // endregion
}
