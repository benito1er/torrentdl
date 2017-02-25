package net.myonlinestuff.torrentdl.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.myonlinestuff.torrentdl.domain.ShowLink;
import net.myonlinestuff.torrentdl.parser.SiteParser;

@Service
public class Parser {

    @Autowired
    private List<SiteParser> siteParsers;

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    private final Map<String, SiteParser> siteParserMap = new HashMap<>();

    public List<ShowLink> parseRootPage(String[] urls) {

        LOGGER.info("parsing root site: {}", Arrays.toString(urls));
        final List<ShowLink> showLinks = new ArrayList<>();
        for (final String url : urls) {
            Assert.hasText(url);
            final String urlRoot = StringUtils.substringBefore(StringUtils.replace(url, "http://", ""), "/");
            SiteParser urlSiteparser = siteParserMap.get(urlRoot);
            if (urlSiteparser == null) {
                urlSiteparser = SiteParserUtils.getSiteParserByUrl(siteParsers, url);
                if (urlSiteparser != null) {
                    urlSiteparser.setUrlRoot("http://" + urlRoot);
                    urlSiteparser.initCoockies();
                }
                siteParserMap.put(urlRoot, urlSiteparser);
            }
            if (urlSiteparser == null) {
                continue;
            }
            Document document = null;
            try {

                // document = Jsoup.connect(url).get();
                document = urlSiteparser.getMainDocument(url);
            } catch (final SocketTimeoutException e) {
                continue;
            } catch (final IOException e) {
                continue;
            }
            Assert.notNull(document);

            final Elements el = urlSiteparser.getElementAhref(document);
            // if (StringUtils.contains(url, "cpasbien")) {
            // Elements el = document.select("div.ligne0 a");
            // addShowLink(url, showLinks, el);
            // el = document.select("div.ligne1 a");
            // } else {
            // final Elements el = document.select("td a");
            // }
            addShowLink(url, showLinks, el);
        }

        return showLinks;

    }

    private void addShowLink(String url, List<ShowLink> showLinks, Elements elements) {
        if (elements != null && !elements.isEmpty()) {
            LOGGER.info("element found: {}", elements.size());
            for (final Element element : elements) {
                final String elementHref = element.attr("href");
                if (StringUtils.isBlank(elementHref)) {
                    continue;
                }
                final String showLinkName = element.text();
                showLinks.add(new ShowLink(showLinkName, elementHref, url));
            }
        }
    }

    public List<String> parseShowPage(String pageUrl) {
        String torrentUrl = null;
        LOGGER.info("parsing show page site: {}", pageUrl);
        Assert.hasText(pageUrl);
        final String urlRoot = StringUtils.substringBefore(StringUtils.replace(pageUrl, "http://", ""), "/");
        final SiteParser urlSiteparser = siteParserMap.get(urlRoot);
        if (urlSiteparser == null) {
            return new ArrayList<>();
        }
        Document document = null;
        try {
            document = urlSiteparser.getShowPageDocument(pageUrl);
        } catch (final SocketTimeoutException e) {
            return new ArrayList<>();
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", pageUrl, e);
            return new ArrayList<>();
        }

        Assert.notNull(document);

        final Elements links = urlSiteparser.getTorrentElement(document);
        final List<String> torrentUrls = new ArrayList<>();
        if (links != null && !links.isEmpty()) {
            torrentUrl = links.get(0).attr("href");
            if (StringUtils.isNoneBlank(torrentUrl) && !"#".equalsIgnoreCase(torrentUrl)) {
                torrentUrls.add(torrentUrl);
            }
        }
        return torrentUrls;
    }

}
