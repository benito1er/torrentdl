package net.myonlinestuff.torrentdl.parser;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public interface SiteParser {

    public String getMatchingUrl();
    public Elements getElementAhref(Document document);

    public Elements getTorrentElement(Document document);

    public void setUrlRoot(String urlRoot);

    public Document getMainDocument(String url) throws SocketTimeoutException, IOException;

    public void initCoockies();

    public Document getShowPageDocument(String pageUrl) throws SocketTimeoutException, IOException;

}
