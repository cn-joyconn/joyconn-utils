package cn.joyconn.utils.elasticsearch.highclient;


import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.core.TimeValue;
import org.springframework.stereotype.Service;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.DeprecationHandler;
import org.elasticsearch.xcontent.NamedXContentRegistry;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Service
public class RestHighLevelClientService {
    @Autowired
    private RestHighLevelClient client;


    /**
     * 创建索引
     * @param indexName
     * @param settings
     * @param mapping
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String indexName, String settings, String mapping) throws IOException{

        CreateIndexRequest request = new CreateIndexRequest(indexName);

        if (null != settings && !"".equals(settings)) {
            XContentParser parser = XContentFactory.xContent(XContentType.JSON). createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, settings);

            request.settings(parser.map());
        }
        if (null != mapping && !"".equals(mapping)) {
            XContentParser parser = XContentFactory.xContent(XContentType.JSON). createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, mapping);

            request.mapping(parser.map());
        }
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引
     * @param indexNames
     * @return
     * @throws IOException
     */
    public AcknowledgedResponse deleteIndex(String ... indexNames) throws IOException{
        DeleteIndexRequest request = new DeleteIndexRequest(indexNames);
        return client.indices().delete(request, RequestOptions.DEFAULT);
    }


    /**
     * 判断 index 是否存在
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean indexExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 判断 index 中是否存在文档（按ID）
     * @param indexName
     * @param id
     * @return
     * @throws IOException
     */
    public boolean existsDoc(String indexName, String id) throws IOException{
        GetRequest request = new GetRequest(indexName, id);
        return client.exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据 id 删除指定索引中的文档
     * @param indexName
     * @param id
     * @return
     * @throws IOException
     */
    public DeleteResponse deleteDoc(String indexName, String id) throws IOException{
        DeleteRequest request = new DeleteRequest(indexName, id);
        return client.delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 添加文档 使用自动id
     * @param indexName
     * @param source
     * @return
     * @throws IOException
     */
    public IndexResponse addDoc(String indexName, String source) throws IOException{
        IndexRequest request = new IndexRequest(indexName);
        XContentParser parser = XContentFactory.xContent(XContentType.JSON). createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, source);

        request.source(parser.map());

        return client.index(request, RequestOptions.DEFAULT);
    }
    /**
     * 添加文档 手动指定id
     * @param indexName
     * @param id
     * @param source
     * @return
     * @throws IOException
     */
    public IndexResponse addDoc(String indexName, String id, String source) throws IOException{
        IndexRequest request = new IndexRequest(indexName);
        XContentParser parser = XContentFactory.xContent(XContentType.JSON). createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, source);

        request.id(id).source(parser.map());

        return client.index(request, RequestOptions.DEFAULT);
    }


    /**
     * 根据 id 更新指定索引中的文档
     * @param indexName
     * @param id
     * @return
     * @throws IOException
     */
    public UpdateResponse updateDoc(String indexName, String id, String updateJson) throws IOException{
        UpdateRequest request = new UpdateRequest(indexName, id);
        XContentParser parser = XContentFactory.xContent(XContentType.JSON). createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, updateJson);
        request.doc(parser.map());
        return client.update(request, RequestOptions.DEFAULT);
    }
    /**
     * 根据 id 更新指定索引中的文档
     * @param indexName
     * @param id
     * @return
     * @throws IOException
     */
    public UpdateResponse updateInsertDoc(String indexName, String id, String updateJson) throws IOException{
        IndexRequest indexRequest = new IndexRequest(indexName);
        XContentParser parser = XContentFactory.xContent(XContentType.JSON). createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, updateJson);
        indexRequest.id(id).source(parser.map());
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        updateRequest.doc(parser.map()).upsert(indexRequest);
        return client.update(updateRequest, RequestOptions.DEFAULT);

    }
    /**
     * 根据 id 更新指定索引中的文档
     * @param indexName
     * @param id
     * @return
     * @throws IOException
     */
    public UpdateResponse updateDoc(String indexName, String id, Map<String,Object> updateMap) throws IOException{
        UpdateRequest request = new UpdateRequest(indexName, id);
        request.doc(updateMap);
        return client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据某字段的 k-v 更新索引中的文档
     * @param fieldName
     * @param value
     * @param indexName
     * @throws IOException
     */
    public void updateByQuery(String fieldName, String value, String ... indexName) throws IOException {
        UpdateByQueryRequest request = new UpdateByQueryRequest(indexName);
        //单次处理文档数量
        request.setBatchSize(100)
                .setQuery(new TermQueryBuilder(fieldName, value))
                .setTimeout(TimeValue.timeValueMinutes(2));
        client.updateByQuery(request, RequestOptions.DEFAULT);
    }



    /**
     * 模糊匹配 默认分页为 0,10
     * @param field
     * @param key
     * @param page
     * @param size
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse search(String field, String key, int page, int size, String ... indexNames) throws IOException{
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new MatchQueryBuilder(field, key))
                .from((page-1)*size)
                .size(size);
        request.source(builder);
        return client.search(request, RequestOptions.DEFAULT);
    }
    /**
     * 高级配
     * @param builder
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse search( SearchSourceBuilder builder, String ... indexNames) throws IOException{
        SearchRequest request = new SearchRequest(indexNames);
        request.source(builder);
        return client.search(request, RequestOptions.DEFAULT);
    }
    /**
     * term 查询 精准匹配
     * @param field
     * @param key
     * @param page
     * @param size
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse termSearch(String field, String key, int page, int size, String ... indexNames) throws IOException{
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termsQuery(field, key))
                .from((page-1)*size)
                .size(size);
        request.source(builder);
        return client.search(request, RequestOptions.DEFAULT);
    }


    /**
     * 批量导入
     * @param indexName
     * @param isAutoId 使用自动id 还是使用传入对象的id
     * @param source
     * @return
     * @throws IOException
     */
    public BulkResponse importAll(String indexName, boolean isAutoId,  String ... source) throws IOException{
        if (0 == source.length){
            //todo 抛出异常 导入数据为空
        }
        BulkRequest request = new BulkRequest();
        if (isAutoId) {
            for (String s : source) {
                request.add(new IndexRequest(indexName).source(s, XContentType.JSON));
            }
        } else {
            for (String s : source) {
                request.add(new IndexRequest(indexName).id(JSONObject.parseObject(s).getString("id")).source(s, XContentType.JSON));
            }
        }
        return client.bulk(request, RequestOptions.DEFAULT);
    }
}
