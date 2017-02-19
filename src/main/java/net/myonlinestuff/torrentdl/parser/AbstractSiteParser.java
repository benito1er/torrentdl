package net.myonlinestuff.torrentdl.parser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSiteParser implements SiteParser {
    protected String urlRoot;
    protected Map<String, String> coockies;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSiteParser.class);
    
    @Override
    public String getMatchingUrl() {
        return StringUtils.substringBefore(StringUtils.substringAfterLast(this.getClass().getName(), "."), "SiteParser");
    }

    @Override
    public Elements getElementAhref(Document document) {
        return document.select("td a");
    }

    @Override
    public Elements getTorrentElement(Document document) {
        return document.select("a");
    }

    @Override
    public void setUrlRoot(String urlRoot) {
        this.urlRoot = urlRoot;
    }

    @Override
    public Document getMainDocument(String url) throws SocketTimeoutException, IOException {
        Document document = null;
        try {

            document = Jsoup.connect(url).get();
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", url, e);
            throw e;
        }
        return document;
    }

    @Override
    public void initCoockies(){
        // throw new NotImplementedException("Not availiable for this parser");
    }

    @Override
    public Document getShowPageDocument(String pageUrl) throws SocketTimeoutException, IOException {
        Document document = null;
        try {
            document = Jsoup.connect(pageUrl).get();
            return document;
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", pageUrl, e);
            throw e;
        }

    }
}
