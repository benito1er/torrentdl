package net.myonlinestuff.torrentdl.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSiteParser implements SiteParser {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
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
    public void initCoockies() {
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

    @Override
    public String getCoockiesForUrlConn() {
        if (coockies == null || coockies.isEmpty()) {
            return null;
        } else {
            final StringBuilder sb = new StringBuilder();
            for (final Map.Entry<String, String> coockieEntry : coockies.entrySet()) {
                sb.append(coockieEntry.getKey()).append("=").append(coockieEntry.getValue()).append(";");
            }
            return sb.toString();
        }
    }

    @Override
    public URLConnection getTorrentURLConnection(final String torrentUrl) throws MalformedURLException, IOException {
            final URL torrentURL = new URL(torrentUrl);
            final URLConnection conn = torrentURL.openConnection();
            if (StringUtils.isNotBlank(this.getCoockiesForUrlConn())) {
                conn.setRequestProperty("Cookie", this.getCoockiesForUrlConn());
                conn.setRequestProperty("User-Agent", AbstractSiteParser.USER_AGENT);
            }
            return  conn;

    }
}
