package com.lian.shai.currencymonitor.dao;

import java.net.InetAddress;
import java.net.URL;

public class Request {
    private long date;
    private String crawlerType;
    private String id;
    private String url;
    private Report report;

    public Request(String crawlerType, String id, String url) {
        setCrawlerType(crawlerType);
        this.id = id;
        this.url = url;

        String ip = null;
        try {
            ip = InetAddress.getByName(new URL(url).getHost()).getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.report = new Report(id, url, crawlerType, ip);
        this.date = System.currentTimeMillis();
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getCrawlerType() {
        return crawlerType;
    }

    public void setCrawlerType(String crawlerType) {
        this.crawlerType = crawlerType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
