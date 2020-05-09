package com.lian.shai.currencymonitor.data;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IpProvider {

    @Value("${ip.germany}")
    private String ipGermany;

    @Value("${ip.china}")
    private String ipChina;

    private List<IpWrapper> ips;

    public IpProvider() {
        ips = new ArrayList<>();
        this.ips.add(new IpWrapper("Germany", "2.16.61.8"));
        this.ips.add(new IpWrapper("China", "14.102.128.10"));
    }

    public IpWrapper getIp() {
        int rand = new Random().nextInt(2);
        return ips.get(rand);
    }
}
