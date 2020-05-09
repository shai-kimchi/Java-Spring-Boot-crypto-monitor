package com.lian.shai.currencymonitor.controller;

import com.lian.shai.currencymonitor.businesslogic.CurrencyMonitorService;
import com.lian.shai.currencymonitor.dao.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class CurrencyMonitorController {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyMonitorController.class);

    @Autowired
    private CurrencyMonitorService service;

    @GetMapping("/requests")
    public ResponseEntity<List<Request>> getAllRequests() {
        List<Request> requests;
        try {
            requests = service.getAllRequests();
        }
        catch(Exception e) {
            logger.error("Could not retrieve requests", e);
            return ResponseEntity.status(BAD_REQUEST).build();
        }

        return ResponseEntity.ok().body(requests);
    }

    @PostMapping("/crawler/{activity}")
    public ResponseEntity<Request> crawl(@PathVariable String activity,
                                         @RequestParam (value = "url", required = true) String url) {
        Request request;
        try {
            request = service.analyzeWebsite(url, activity);
        }
        catch(Exception e) {
            logger.error("Could not complete the process", e);
            return ResponseEntity.status(BAD_REQUEST).build();
        }

        return ResponseEntity.ok().body(request);
    }

    /*@PostMapping("/crawl-forgery")
    public ResponseEntity<Request> crawlForForgery(String url) {
        Request request;
        try {
            request = service.analyzeWebsite(url, "forgery");
        }
        catch(Exception e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok().body(request);
    }

    @PostMapping("/crawl-trade")
    public ResponseEntity<String> crawlForTrade(String url) {
        try {
            service.analyzeWebsite(url, "trade");
        }
        catch(Exception e) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok().body("OK");
    }*/
}
