package com.lian.shai.currencymonitor.data;

import com.lian.shai.currencymonitor.dao.RawData;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private RawData data;
    private String url;
    //private boolean visited;
    private List<Node> children;

    public Node() {
    }

    public Node(String url) {
        this.url = url;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RawData getData() {
        return data;
    }

    public void setData(RawData data) {
        this.data = data;
    }

//    public boolean isVisited() {
//        return visited;
//    }
//
//    public void setVisited(boolean visited) {
//        this.visited = visited;
//    }
}
