package cn.joyconn.utils.netutils;

import java.util.List;

/**
 * Created by Eric.Zhang on 2017/1/10.
 */
public class HttpResponseModel {
    public HttpResponseModel(){

    }
    List<String> contentCollection;
    String urlString ;
    Integer defaultPort;
    String file ;
    String host;
    String path;
    Integer port;
    String protocol;
    String query ;
    String ref ;
    String userInfo ;
    String content ;
    String contentEncoding ;
    Integer code ;
    String message ;
    String contentType;
    String method ;
    Integer connectTimeout ;
    Integer readTimeout;
    public void setContentCollection(List<String> contentCollection){
        this.contentCollection=contentCollection;
    }
    public List<String> getContentCollection(){
        return this.contentCollection;
    }
    public String getUrlString(){
        return this.urlString;
    }
    public String getFile(){
        return file;
    }
    public String getHost(){
        return host;
    }
    public String getPath(){
        return path;
    }
    public  String getProtocol(){
        return protocol;
    }
    public String getQuery(){
        return query;
    }
    public String getRef(){
        return ref;
    }
    public String getUserInfo(){
        return userInfo;
    }
    public String getContent(){
        return content;
    }
    public String getContentEncoding(){
        return contentEncoding;
    }
    public String getMessage(){
        return message;
    }
    public String getContentType(){
        return contentType;
    }
    public  String getMethod(){
        return method;
    }
    public Integer getDefaultPort(){
        return defaultPort;
    }
    public Integer getPort(){
        return port;
    }
    public Integer getCode(){

        return code;
    }
    public Integer getConnectTimeout(){
        return  connectTimeout;
    }
    public Integer getReadTimeout(){
        return readTimeout;
    }
}

