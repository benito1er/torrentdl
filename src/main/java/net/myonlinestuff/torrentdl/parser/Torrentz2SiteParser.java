package net.myonlinestuff.torrentdl.parser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Torrentz2SiteParser extends AbstractSiteParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Torrent9SiteParser.class);

    @Override
    public Document getMainDocument(String url) throws SocketTimeoutException, IOException {
        Document document = null;
        try {
            document = Jsoup.connect(url).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).get();
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", url, e);
            coockies = getCoockies();
            String url2 = null;
            try {
                url2 = StringUtils.replace(url, "%20", " ");
                document = Jsoup.connect(url2).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).header("PE-Token", "694bce2767bd788372ff0618982ae769a9fee9d4-1487367347-1800").get();
            } catch (final IOException e1) {
                LOGGER.error("Error while getting site as document :{}", url2, e);
                throw e1;
            }

        }
        return document;
    }

    @Override
    public Elements getElementAhref(Document document) {
        return document.select("div.results a");
    }

    @Override
    public Elements getTorrentElement(Document document) {
        return document.select("div.download a");
    }

    @Override
    public void initCoockies() {
        coockies = getCoockies();
    }

    private Map<String, String> getCoockies() {
        Response res = null;
        final String ua = USER_AGENT;
        try {
            res = Jsoup.connect(urlRoot).userAgent(ua).method(Method.POST).execute();
            return res.cookies();
        } catch (final IOException e) {
            return new HashMap<>();
        }
    }

    @Override
    public Document getShowPageDocument(String pageUrl) throws SocketTimeoutException, IOException {
        Document document = null;
        if (coockies.isEmpty()) {
            coockies = getCoockies();
        }
        try {
            document = Jsoup.connect(pageUrl).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).get();
            return document;
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", pageUrl, e);
            throw e;
        }

    }

}
