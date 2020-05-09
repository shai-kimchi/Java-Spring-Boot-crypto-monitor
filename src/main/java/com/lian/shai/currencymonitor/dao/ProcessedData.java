package com.lian.shai.currencymonitor.dao;

import java.util.Date;
import java.util.List;

public class ProcessedData {

    public enum ActivityType {
        FRAUD, TRADE, FORGERY, NONE;
    }

    private String url;
    private String forumName;
    private String ip;
    private List<String> userNames;
    private ActivityType activityType;
    private List<String> dates;
    private int severity;
    private String guid;
    private String country;

    public ProcessedData(RawData rawData, String activityType, int severity, String uuid) {
        this.ip = rawData.getIp();
        this.url = rawData.getUrl();
        this.country = rawData.getCountry();
        this.activityType = setActivityTypeByString(activityType);
        this.severity = severity;
        this.guid = uuid;
    }

    private ActivityType setActivityTypeByString(String activityType) {
        switch(activityType) {
            case "fraud":
                return ActivityType.FRAUD;
            case "trade":
                return ActivityType.TRADE;
            case "forgery":
                return ActivityType.FORGERY;
            default:
                return ActivityType.NONE;
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    //    public List<ActivityType> getActivityTypes() {
//        return activityTypes;
//    }
//
//    public void setActivityTypes(List<ActivityType> activityTypes) {
//        this.activityTypes = activityTypes;
//    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
