package com.lian.shai.currencymonitor.data;

import com.lian.shai.currencymonitor.dao.RawData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebCrawler {
    private String url;
    private Set<String> visited;

    @Autowired
    private ElasticsearchClient elasticClient;

    //private final String URL_REGEX = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private final String URL_REGEX = "(https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*";
    private final Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    public WebCrawler() {
        this.visited = new HashSet<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void crawl(Node node, int depth, String uuid, IpWrapper ipWrapper) {
        crawlHelper(node, depth, uuid, ipWrapper);
        this.visited.clear();
    }

    private void crawlHelper(Node node, int depth, String uuid, IpWrapper ipWrapper) { // recursive dfs
        this.url = node.getUrl();

        try {
            Document doc = Jsoup.connect(node.getUrl()).get();
            Elements links = doc.select("a[href]");

            Node currentNode = new Node();
            RawData rawData = new RawData();
            rawData.setGuid(uuid);

            currentNode.setChildren(links.stream().map(l->new Node(l.attr("abs:href")))
                    .collect(Collectors.toList()));

            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (validateLink(link, absUrl) && !visited.contains(absUrl) && depth >= 0) {
                    visited.add(absUrl);
                    rawData.setText(doc.body().text());
                    rawData.setHtmlContent(doc.html());
                    rawData.setUrl(absUrl);
                    rawData.setIp(InetAddress.getByName(new URL(url).getHost()).getHostAddress());
                    //IpWrapper ipWrapper = ipProvider.getIp();
                    //rawData.setIp(ipWrapper.getIp());
                    rawData.setCountry(ipWrapper.getCountry());
                    currentNode.setData(rawData);
                    elasticClient.saveDocumentRawData(rawData);
                    crawlHelper(new Node(absUrl), depth-1, uuid, ipWrapper);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private boolean validateLink(Element link, String absUrl) throws URISyntaxException {
        return checkRegex(absUrl) && !link.attr("href").equals("#") && sameDomain(absUrl);
    }

    private boolean checkRegex(String absUrl) {
        return Pattern.compile(URL_REGEX).matcher(absUrl).matches();
    }

    private boolean sameDomain(String linkUrl) throws URISyntaxException {
        String mainDomain = new URI(url).getHost();
        String domain = new URI(linkUrl).getHost();
        return domain.equalsIgnoreCase(mainDomain);
    }
}
