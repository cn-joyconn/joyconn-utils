//package top.mortise.utils.elasticsearch.highclient;
//
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.elasticsearch.client.Node;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RestHighLevelClientConfig {
//    @Value("${mortise.elasticsearch.simpleUtil.ip:127.0.0.1}")
//    String host;
//    @Value("${mortise.elasticsearch.simpleUtil.port:9200}")
//    int port;
//    @Value("${mortise.elasticsearch.rest.username:elastic}")
//    String userName;
//    @Value("${mortise.elasticsearch.rest.password:TaPedBAwX71aYmL4tuUA}")
//    String password;
//
//
//
////    @Bean
////    public RestClientBuilder restClientBuilder() {
////        //凭证注册器
////        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
////
////        //注册
////        credentialsProvider.setCredentials(AuthScope.ANY,
////                new UsernamePasswordCredentials(userName, password));
////        RestClientBuilder restClientBuilder = RestClient.builder(
////                new HttpHost(host, port, "http")
////        ) .setHttpClientConfigCallback(httpAsyncClientBuilder ->
////                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
////
////        restClientBuilder.setFailureListener(new RestClient.FailureListener(){
////            @Override
////            public void onFailure(Node node) {
////                System.out.println("监听某个es节点失败");
////            }
////        });
////
////        restClientBuilder.setRequestConfigCallback(builder ->
////                builder.setConnectTimeout(5000).setSocketTimeout(15000));
////
////        return restClientBuilder;
////    }
////
////
////
////    @Bean
////    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
////        return new RestHighLevelClient(restClientBuilder);
////    }
//    @Bean
//    public RestHighLevelClient cient() {
//        //凭证注册器
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//
//        //注册
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(userName, password));
//        RestClientBuilder restClientBuilder = RestClient.builder(
//                new HttpHost(host, port, "http")
//        ) .setHttpClientConfigCallback(httpAsyncClientBuilder ->
//                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
//
//        restClientBuilder.setFailureListener(new RestClient.FailureListener(){
//            @Override
//            public void onFailure(Node node) {
//                System.out.println("监听某个es节点失败");
//            }
//        });
//
//        restClientBuilder.setRequestConfigCallback(builder ->
//                builder.setConnectTimeout(5000).setSocketTimeout(15000));
//        return new RestHighLevelClient(restClientBuilder);
//    }
//}
