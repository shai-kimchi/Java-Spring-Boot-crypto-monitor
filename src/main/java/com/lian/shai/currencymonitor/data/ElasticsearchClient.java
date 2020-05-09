package com.lian.shai.currencymonitor.data;

import com.lian.shai.currencymonitor.dao.ProcessedData;
import com.lian.shai.currencymonitor.dao.RawData;
import com.lian.shai.currencymonitor.dao.Request;

import java.io.IOException;
import java.util.List;

public interface ElasticsearchClient {
    void saveDocumentRawData(RawData data) throws IOException;
    void saveDocumentProcessedData(ProcessedData data);
    List<String> getAllDocumentIds() throws IOException;
    List<DocumentWords> countWords(List<String> ids) throws IOException;
    RawData getRawDataById(String id) throws IOException;
    void saveRequest(Request request);
    void clearRawData();
    void createRawDataIndex();
    List<Request> getAllDocumentsByIndex(String indexName);
}
