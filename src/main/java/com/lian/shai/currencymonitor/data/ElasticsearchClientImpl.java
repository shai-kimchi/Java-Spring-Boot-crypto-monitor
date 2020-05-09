package com.lian.shai.currencymonitor.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lian.shai.currencymonitor.dao.ProcessedData;
import com.lian.shai.currencymonitor.dao.RawData;
import com.lian.shai.currencymonitor.dao.Request;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElasticsearchClientImpl implements ElasticsearchClient {
    private final Logger logger = LoggerFactory.getLogger(ElasticsearchClientImpl.class);
    private Gson gson = new GsonBuilder().create();

    @Value("${elastic.type.name}")
    private String elasticTypeNameRawData;

    @Value("${elastic.index.name.raw.data}")
    private String elasticIndexNameRawData;

    @Value("${elastic.type.name.processed.data}")
    private String elasticTypeNameProcessedData;

    @Value("${elastic.index.name.processed.data}")
    private String elasticIndexNameProcessedData;

    @Value("${elastic.type.name.requests}")
    private String elasticTypeNameRequests;

    @Value("${elastic.index.name.requests}")
    private String elasticIndexNameRequests;

    @Autowired
    private RestHighLevelClient elasticClient;

    public ElasticsearchClientImpl() {
    }

    @Override
    public void saveDocumentRawData(RawData data) {
        try {
            String rawDataJson = gson.toJson(data);
            logger.info(
                    "injecting raw data with url: {} to index table: {}, with type: {}", data.getUrl(), elasticIndexNameRawData,
                    elasticTypeNameRawData);
            IndexRequest request = new IndexRequest(elasticIndexNameRawData);
            request.source(rawDataJson, XContentType.JSON);
            IndexResponse response = elasticClient.index(request, RequestOptions.DEFAULT);
            logger.info("injection result from client: {}", response.getResult());

            if (response.getShardInfo().getFailed() > 0) {
                Arrays.asList(response.getShardInfo().getFailures()).stream().forEach(failure -> logger.error("failed to inject due to {}", failure.getCause()));
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void saveDocumentProcessedData(ProcessedData data) {
        try {
            String processedDataJson = gson.toJson(data);
            logger.info(
                    "injecting processed data with url: {} to index table: {}, with type: {}", data.getUrl(), elasticIndexNameProcessedData,
                    elasticTypeNameProcessedData);
            IndexRequest request = new IndexRequest(elasticIndexNameProcessedData);
            request.source(processedDataJson, XContentType.JSON);
            IndexResponse response = elasticClient.index(request, RequestOptions.DEFAULT);
            logger.info("injection result from client: {}", response.getResult());

            if (response.getShardInfo().getFailed() > 0) {
                Arrays.asList(response.getShardInfo().getFailures()).stream().forEach(failure -> logger.error("failed to inject due to {}", failure.getCause()));
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public List<String> getAllDocumentIds() throws IOException {
        if (isIndexExists(elasticIndexNameRawData)) {
            SearchRequest searchRequest = new SearchRequest(elasticIndexNameRawData);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);

            SearchHit[] hits = elasticClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
            return Stream.of(hits).map(hit -> String.valueOf(hit.getId())).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<DocumentWords> countWords(List<String> ids) throws IOException {
        List<TermVectorsResponse> responses = new ArrayList<>();
        for (String id : ids) {
            TermVectorsRequest request = new TermVectorsRequest(elasticIndexNameRawData, id);
            request.setFields("text");
            request.setTermStatistics(true);
            responses.add(elasticClient.termvectors(request, RequestOptions.DEFAULT));
        }

        return responses.stream().map(tv -> {
            DocumentWords documentWords = new DocumentWords(tv.getId());
            documentWords.addTermVector(tv);
            return documentWords;
        }).collect(Collectors.toList());
    }

    @Override
    public RawData getRawDataById(String id) throws IOException {
        GetRequest getRequest = new GetRequest(elasticIndexNameRawData, id);
        GetResponse getResponse = elasticClient.get(getRequest, RequestOptions.DEFAULT);
        String rawDataJson = getResponse.getSourceAsString();
        return gson.fromJson(rawDataJson, RawData.class);
    }

    @Override
    public void saveRequest(Request theRequest) {
        try {
            String requestJson = gson.toJson(theRequest);
            logger.info(
                    "injecting request with uuid: {} to index table: {}, with type: {}",
                    theRequest.getId(),
                    elasticIndexNameRequests,
                    elasticTypeNameRequests);
            IndexRequest request = new IndexRequest(elasticIndexNameRequests);
            request.source(requestJson, XContentType.JSON);
            IndexResponse response = elasticClient.index(request, RequestOptions.DEFAULT);
            logger.info("injection result from client: {}", response.getResult());

            if (response.getShardInfo().getFailed() > 0) {
                Arrays.asList(response.getShardInfo().getFailures()).stream().forEach(failure -> logger.error("failed to inject due to {}", failure.getCause()));
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void clearRawData() {
        try {
            if (isIndexExists(elasticIndexNameRawData)) {
                DeleteIndexRequest request = new DeleteIndexRequest(elasticIndexNameRawData);
                elasticClient.indices().delete(request, RequestOptions.DEFAULT);
                logger.info("table {} was deleted successfully", elasticIndexNameRawData);
            }
        } catch (IOException e) {
            logger.error("failed to delete table {}. error: {}", elasticIndexNameRawData, e.getMessage());
        }
    }

    @Override
    public void createRawDataIndex() {
        try {
            //create index and put mapping
            if (!isIndexExists(elasticIndexNameRawData)) {
                CreateIndexRequest request = new CreateIndexRequest(elasticIndexNameRawData);
                String mapping = getStringFromFile("JsonMappingRawdata");
                request.mapping(mapping, XContentType.JSON);
                elasticClient.indices().create(request, RequestOptions.DEFAULT);
            }

        } catch (IOException e) {
            logger.error("failed to create table {}. error: {}", elasticIndexNameRawData, e.getMessage());
        }
    }

    @Override
    public List<Request> getAllDocumentsByIndex(String indexName) {
        if (isIndexExists(indexName)) {
            try {
                SearchRequest searchRequest = new SearchRequest(indexName);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(QueryBuilders.matchAllQuery());
                searchSourceBuilder.sort(new FieldSortBuilder("date").order(SortOrder.DESC));
                searchRequest.source(searchSourceBuilder);

                SearchHit[] hits = elasticClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
                return Stream.of(hits)
                        .map(hit -> gson.fromJson(hit.getSourceAsString(), Request.class))
                        .collect(Collectors.toList());

            } catch (Exception e) {
                logger.error("failed to get all documents of table: {}. error: {}", indexName, e);
                return null;
            }
        }
        return null;
    }

    private boolean isIndexExists(String indexName) {
        try {
            GetIndexRequest getRequest = new GetIndexRequest();
            getRequest.indices(indexName);
            var isExists = elasticClient.indices().exists(getRequest, RequestOptions.DEFAULT);
            if (!isExists) {
                logger.error("index: {} does not exist", indexName);
            }
            return isExists;
        } catch (IOException e) {
            logger.error("failed to check if table: {} exists. error: {}", elasticIndexNameRawData, e.getMessage());
        }
        return false;
    }

    private String getStringFromFile(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream in = classLoader.getResourceAsStream(fileName);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        var res = result.toString(StandardCharsets.UTF_8.name());
        return res;
        /*File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        return new String(data, "UTF-8");*/
    }
}
