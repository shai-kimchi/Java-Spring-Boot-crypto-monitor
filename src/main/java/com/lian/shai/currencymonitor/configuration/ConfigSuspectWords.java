package com.lian.shai.currencymonitor.configuration;

import com.lian.shai.currencymonitor.dao.ProcessedData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigSuspectWords {

    private Map<String,Integer> suspectFraudWordsMap;
    private Map<String,Integer> suspectTradeWordsMap;
    private Map<String,Integer> suspectForgeryWordsMap;

    public ConfigSuspectWords() throws URISyntaxException, IOException {
        suspectFraudWordsMap = initMaps("suspectFraudWords.txt");
        suspectTradeWordsMap = initMaps("suspectTradeWords.txt");
        suspectForgeryWordsMap = initMaps("suspectForgeryWords.txt");
    }

    public Map<String, Integer> getSuspectWordsMapByActivityType(String activityType) {
        switch(activityType) {
            case "fraud":
                return suspectFraudWordsMap;
            case "trade":
                return suspectTradeWordsMap;
            case "forgery":
                return suspectForgeryWordsMap;
            default:
                return suspectFraudWordsMap;
        }
    }

    public Map<String, Integer> getSuspectFraudWordsMap() {
        return suspectFraudWordsMap;
    }

    public Map<String, Integer> getSuspectTradeWordsMap() {
        return suspectTradeWordsMap;
    }

    public Map<String, Integer> getSuspectForgeryWordsMap() {
        return suspectForgeryWordsMap;
    }

    private Map<String, Integer> initMaps(String fileName) throws URISyntaxException, IOException {
        URL suspectWordsUrl = getClass().getClassLoader().getResource(fileName);
        List<String> words = Files.readAllLines(Path.of(suspectWordsUrl.toURI()));
        return words.stream()
                .collect(Collectors.toMap(word -> word.split(",")[0], word -> Integer.valueOf(word.split(",")[1])));
    }
}
