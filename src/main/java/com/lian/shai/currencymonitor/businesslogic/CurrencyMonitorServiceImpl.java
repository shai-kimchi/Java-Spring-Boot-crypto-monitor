package com.lian.shai.currencymonitor.businesslogic;

import com.lian.shai.currencymonitor.configuration.ConfigSuspectWords;
import com.lian.shai.currencymonitor.dao.ProcessedData;
import com.lian.shai.currencymonitor.dao.RawData;
import com.lian.shai.currencymonitor.dao.Request;
import com.lian.shai.currencymonitor.data.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CurrencyMonitorServiceImpl implements CurrencyMonitorService {

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchClientImpl.class);

    @Autowired
    private WebCrawler crawler;

    @Autowired
    private ConfigSuspectWords suspectWords;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${max.depth}")
    private int maxDepth;

    @Value("${elastic.type.name}")
    private String elasticType;

    @Value("${elastic.index.name.raw.data}")
    private String indexNameRawData;

    @Value("${elastic.index.name.processed.data}")
    private String indexNameProcessedData;

    @Value("${elastic.index.name.requests}")
    private String indexNameRequests;

    @Value("${suspicion.threshold}")
    private int threshold;

    @Autowired
    private IpProvider ipProvider;

    @Override
    public Request analyzeWebsite(String url, String activityType) throws IOException {
        elasticsearchClient.clearRawData();
        String uuid = UUID.randomUUID().toString();
        //InetAddress country = InetAddress.getByName(ip);
        IpWrapper ipWrapper = ipProvider.getIp();
        Request request = new Request(activityType, uuid, url);
        crawler.crawl(new Node(url), maxDepth, uuid, ipWrapper);
        processData(activityType, uuid, request);
        elasticsearchClient.saveRequest(request);
        return request;
    }

    @Override
    public List<Request> getAllRequests() {
        List<Request> requests = elasticsearchClient.getAllDocumentsByIndex(indexNameRequests);
        return requests;
    }

    /*private void initRawDataIndex() {
        elasticsearchClient.createRawDataIndex();
    }*/

    private void processData(String activityType, String uuid, Request request) throws IOException {
        // iterate over docs in elasticsearch and compute threshold
        List<String> ids = elasticsearchClient.getAllDocumentIds();
        if (ids != null) {
            List<DocumentWords> documentWords = elasticsearchClient.countWords(ids);
            Map<String, Integer> suspectWordsMap;

            suspectWordsMap = suspectWords.getSuspectWordsMapByActivityType(activityType);
            for (DocumentWords doc : documentWords) {
                int severity = isDocumentSuspicious(suspectWordsMap, doc);
                if (severity >= threshold) {
                    RawData rawData = elasticsearchClient.getRawDataById(doc.getId());
                    ProcessedData processedData = new ProcessedData(rawData, activityType, severity, uuid);
                    processedData.setForumName(extractForumName(rawData.getHtmlContent()));
                    processedData.setUserNames(extractUserNames(rawData.getHtmlContent()));
                    processedData.setDates(extractDates(rawData.getHtmlContent()));
                    elasticsearchClient.saveDocumentProcessedData(processedData);
                    request.getReport().getProcessedData().add(processedData);
                }
            }
        }
        /*for (DocumentWords doc : documentWords) {
            List<ProcessedData.ActivityType> documentActivityTypes = getDocumentActivityTypes(doc);
            if(!documentActivityTypes.contains(ProcessedData.ActivityType.NONE)) {
                RawData rawData = elasticsearchClient.getRawDataById(doc.getId());
                ProcessedData processedData = new ProcessedData(rawData, documentActivityTypes);
                //processedData.setForumName(extractForumName(rawData.getHtmlContent()));
                //processedData.setUserNames(extractUserNames(rawData.getHtmlContent()));
                //processedData.setDates(extractDates(rawData.getHtmlContent()));
                elasticsearchClient.saveDocumentProcessedData(processedData);
            }
        }*/
    }

    private List<String> extractDates(String htmlContent) {
        List<String> dates = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements allDates = doc.select("#date");
        for (Element date : allDates) {
            dates.add(date.text());
        }
        return dates;
    }

    private List<String> extractUserNames(String htmlContent) {
        List<String> userNames = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements userNamesElements = doc.select("#userName");
        for (Element userName : userNamesElements) {
            userNames.add(userName.text());
        }
        return userNames;
    }

    private String extractForumName(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        return doc.title();
    }

    /*private List<ProcessedData.ActivityType> getDocumentActivityTypes(DocumentWords documentWords) {
        List<ProcessedData.ActivityType> activityTypesList = new ArrayList<>();

        Map<String, Integer> suspectFraudWordsMap = suspectWords.getSuspectFraudWordsMap();
        if (isDocumentSuspicious(suspectFraudWordsMap, documentWords)) {
            activityTypesList.add(ProcessedData.ActivityType.FRAUD);
        }

        Map<String, Integer> suspectTradeWordsMap = suspectWords.getSuspectTradeWordsMap();
        if (isDocumentSuspicious(suspectTradeWordsMap, documentWords)) {
            activityTypesList.add(ProcessedData.ActivityType.TRADE);
        }

        Map<String, Integer> suspectForgeryWordsMap = suspectWords.getSuspectForgeryWordsMap();
        if (isDocumentSuspicious(suspectForgeryWordsMap, documentWords)) {
            activityTypesList.add(ProcessedData.ActivityType.FORGERY);
        }

        if (activityTypesList.isEmpty()) {
            activityTypesList.add(ProcessedData.ActivityType.NONE);
        }

        return activityTypesList;
    }*/

    private int isDocumentSuspicious(Map<String, Integer> suspectWordsMap, DocumentWords documentWords) {
        int sum = 0;
        for (Map.Entry<String, Integer> entry : documentWords.getWordCount().entrySet()) {
            if (suspectWordsMap.containsKey(entry.getKey())) {
                sum += suspectWordsMap.get(entry.getKey()) * entry.getValue();
            }
        }

        logger.info("score of document id {} is {}", documentWords.getId(), sum);
        //return sum >= threshold;
        return sum;
    }
}
