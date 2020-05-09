package com.lian.shai.currencymonitor.data;

public class IpWrapper {

    private String country;
    private String ip;

    public IpWrapper(String country, String ip) {
        this.country = country;
        this.ip = ip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
