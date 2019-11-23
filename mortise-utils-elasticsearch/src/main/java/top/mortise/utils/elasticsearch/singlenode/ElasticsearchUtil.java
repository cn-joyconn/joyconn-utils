package top.mortise.utils.elasticsearch.singlenode;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.mortise.utils.loghelper.LogHelper;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class ElasticsearchUtil {
    @Value("${mortise.elasticsearch.simpleUtil.ip:127.0.0.1}")
    String host;
    @Value("${mortise.elasticsearch.simpleUtil.port:9200}")
    int port;



    public  TransportClient getTransportClient() {
        // 解决netty冲突
        // System.setProperty("es.set.netty.runtime.available.processors", "false");
        try {
            //设置集群名称
//            Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
            Settings settings = Settings.builder().put("cluster.name", "realfake-es").build();
            //创建client
            return new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public  RestHighLevelClient getRestHighLevelClient() {
        // 解决netty冲突
        // System.setProperty("es.set.netty.runtime.available.processors", "false");
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, "http")
                ));
    }


    /**
     * 删除文档
     */
    public DeleteResponse deleteById(String index, String type, String documentId) {

        TransportClient client = getTransportClient();
        DeleteResponse response = null;
        if (client == null) {
            return response;
        }
        try {
            DeleteRequestBuilder delete = client.prepareDelete(index, type, documentId);
             response = delete.execute().actionGet();
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {

            client.close();
        }
        return response;

    }


    /**
     * 查询,通过scroll方式，第一次查询返回数据和id
     * 之后查询检测到id不为空则使用scroll快照的方式返回数据，和更新位置后的id
     */
    public SearchResponse searchMutilByScroll( String scrollId,String scroll ) {

        TransportClient client = getTransportClient();
        SearchResponse response =null;
        if (client == null) {
            return null;
        }
        try {
            SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
            // 重新设定id存在时间
            ssrb.setScroll(scroll);
            response = ssrb.execute().actionGet();
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {

            client.close();
        }
        return response;
    }


    /**
     * 查询,通过scroll方式，第一次查询返回数据和id
     * 之后查询检测到id不为空则使用scroll快照的方式返回数据，和更新位置后的id
     */
    public  SearchResponse searchMutilByScroll(String type,int pageSize,Map<String,Boolean> sort,String scroll,   BoolQueryBuilder boolQueryBuilder) {

        TransportClient client = getTransportClient();

        if (client == null) {
            return null;
        }
        SearchResponse response =null;
        try {
            // 如果是第一次搜索，则返回搜索结果和浏览位置id
            // 获取客户端 创建搜索
            // 设置scroll过期时间,2分钟\添加过滤器
            SearchRequestBuilder srb = client.prepareSearch(type).setTypes(type);
            srb.setScroll(scroll).setPostFilter(boolQueryBuilder);

            SearchRequestBuilder searchRequestBuilder = srb.setQuery(
                    boolQueryBuilder
            );
            // 配置每次返回数据量
            if (pageSize == 0) {
                searchRequestBuilder.setSize(10);
            }
            // 排序方式,未指定则用score相关度排序
            if (sort != null) {
                for(String key : sort.keySet()){
                    searchRequestBuilder.addSort(key, sort.get(key) ? SortOrder.DESC : SortOrder.ASC);
                }

            } else {
                searchRequestBuilder.setExplain(true);
            }

            // 执行搜索，
            // 返回搜索response，包含数据、搜索结果数、scrollId等
            response = searchRequestBuilder.execute().actionGet();
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {

            client.close();
        }
        return response;
    }




    /**
     * 验证索引是否存在
     *
     * @param index 索引名称
     * @return
     * @throws Exception
     */
    public boolean indexExists(String index) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();
        boolean exists=false;
        try {
            GetIndexRequest request = new GetIndexRequest();
            request.indices(index);
            request.local(false);
            request.humanReadable(true);

             exists = client.indices().exists(request);
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {

            client.close();
        }

        return exists;
    }

    /**
     * 创建索引
     *
     * @param index
     * @param indexType
     * @param properties 结构: {name:{type:text}} {age:{type:integer}}
     * @return
     * @throws Exception
     */
    public boolean indexCreate(String index, String indexType,
                               Map<String, Object> properties) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();
        boolean acknowledged=false;
        try {
//            if (indexExists(index)) {
//                client.close();
//                return true;
//            }
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(Settings.builder().put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2));

            Map<String, Object> jsonMap = new HashMap<>();
            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);
            jsonMap.put(indexType, mapping);
            request.mapping(indexType, jsonMap);

            CreateIndexResponse createIndexResponse = client.indices().create(
                    request);
            acknowledged = createIndexResponse.isAcknowledged();
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {

            client.close();
        }
        return acknowledged;
    }

    /**
     * 删除索引
     *
     * @param index
     * @return
     * @throws Exception
     */
    public boolean indexDelete(String index) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();
        boolean acknowledged=false;
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(
                    request);
            acknowledged = deleteIndexResponse.isAcknowledged();
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.NOT_FOUND) {
                acknowledged = true;
            } else {
                acknowledged = false;
            }
        } finally {

            client.close();
        }
        return acknowledged;
    }

    /**
     * 根据文档ID，传入json结构，创建或更新文档，
     *
     * @param index
     * @param indexType
     * @param documentId
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public boolean documentCreate(String index, String indexType,
                                  String documentId, String jsonStr) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();

        boolean acknowledged=false;
        try {
            IndexRequest request = new IndexRequest(index, indexType, documentId);

            request.source(jsonStr, XContentType.JSON);
            IndexResponse indexResponse = client.index(request);

            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                    || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                acknowledged = true;
            }
            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                acknowledged = true;
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo
                        .getFailures()) {
                    client.close();
                    throw new Exception(failure.reason());
                }
            }
        } catch (ElasticsearchException exception) {
            LogHelper.logger().error(exception.getMessage());
        } finally {

            client.close();
        }
        return acknowledged;
    }

    /**
     * 传入Map，ID，创建或更新文档
     *
     * @param index
     * @param indexType
     * @param documentId
     * @param map
     * @return
     * @throws Exception
     */
    public boolean documentCreate(String index, String indexType,
                                  String documentId, Map<String, Object> map) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();

        boolean acknowledged=false;
        try {
            IndexRequest request = new IndexRequest(index, indexType, documentId);

            request.source(map);
            IndexResponse indexResponse = client.index(request);

            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                    || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                acknowledged = true;
            }
            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                acknowledged = true;
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo
                        .getFailures()) {
                    client.close();
                    throw new Exception(failure.reason());
                }
            }
        } catch (ElasticsearchException exception) {
            LogHelper.logger().error(exception.getMessage());
        } finally {

            client.close();
        }
        return acknowledged;

    }

    /**
     * 传入json结构数据，创建文档，返回ID
     *
     * @param index
     * @param indexType
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public String documentCreate(String index, String indexType, String jsonStr)
            throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();
        String result=null;
        try {

            IndexRequest request = new IndexRequest(index, indexType);

            request.source(jsonStr, XContentType.JSON);
            IndexResponse indexResponse = client.index(request);

            String id = indexResponse.getId();
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                    || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                client.close();
                result = id;
            }
            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                client.close();
                result = id;
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo
                        .getFailures()) {
                    client.close();
                    throw new Exception(failure.reason());
                }
            }
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {
            client.close();
        }

        return result;
    }

    /**
     * 传入Map，创建文档，返回ID
     *
     * @param index
     * @param indexType
     * @param map
     * @return
     * @throws Exception
     */
    public String documentCreate(String index, String indexType,
                                 Map<String, Object> map) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();
        String result=null;
        try {
            IndexRequest request = new IndexRequest(index, indexType);

            request.source(map);
            IndexResponse indexResponse = client.index(request);

           String  id = indexResponse.getId();
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                    || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                result = id;
            }
            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                result = id;
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo
                        .getFailures()) {
                    client.close();
                    throw new Exception(failure.reason());
                }
            }
        }catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {
            client.close();
        }

        return result;
    }

    public boolean documentDelete(String index, String indexType,
                                  String documentId) throws Exception {

        RestHighLevelClient client = getRestHighLevelClient();
        try {
            DeleteRequest request = new DeleteRequest(index, indexType, documentId);
            DeleteResponse deleteResponse = client.delete(request);
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                client.close();
                return true;
            }
            ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                client.close();
                return true;
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo
                        .getFailures()) {
                    client.close();
                    throw new Exception(failure.reason());
                }
            }
        } catch (Exception ex){
            LogHelper.logger().error(ex.getMessage());
        }finally {

            client.close();
        }

        return false;
    }

    /**
     * 将查询后获得的response转成list
     *
     * @param response
     * @return
     */
    public List responseToList(SearchResponse response) {
        SearchHits hits = response.getHits();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < hits.getHits().length; i++) {
            Map<String, Object> map = hits.getAt(i).getSourceAsMap();
            list.add(map);
        }
        return list;
    }
}
