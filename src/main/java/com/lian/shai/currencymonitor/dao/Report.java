package com.lian.shai.currencymonitor.dao;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String mainUrl;
    private String mainIp;
    private String guid;
    private List<ProcessedData> processedData;
    private String crawlerType;

    public Report(String uid, String url, String crawlerType, String ip) {
        this.guid = uid;
        this.mainUrl = url;
        this.crawlerType = crawlerType;
        this.processedData = new ArrayList<>();
        this.mainIp = ip;
    }

    public String getMainIp() {
        return mainIp;
    }

    public void setMainIp(String mainIp) {
        this.mainIp = mainIp;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public List<ProcessedData> getProcessedData() {
        return processedData;
    }

    public void setProcessedData(List<ProcessedData> processedData) {
        this.processedData = processedData;
    }

    public String getCrawlerType() {
        return crawlerType;
    }

    public void setCrawlerType(String crawlerType) {
        this.crawlerType = crawlerType;
    }
}
