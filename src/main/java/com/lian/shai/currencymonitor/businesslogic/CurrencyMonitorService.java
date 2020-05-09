package com.lian.shai.currencymonitor.businesslogic;

import com.lian.shai.currencymonitor.dao.Request;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface CurrencyMonitorService {
    Request analyzeWebsite(String url, String activityType) throws IOException, URISyntaxException;
    List<Request> getAllRequests();
}
