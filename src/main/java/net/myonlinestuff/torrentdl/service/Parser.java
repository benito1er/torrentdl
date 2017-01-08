package net.myonlinestuff.torrentdl.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.myonlinestuff.torrentdl.domain.ShowLink;

@Service
public class Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    public List<ShowLink> parseRootPage(String[] urls) {
        LOGGER.info("parsing root site: {}", Arrays.toString(urls));
        final List<ShowLink> showLinks = new ArrayList<>();
        for (final String url : urls) {
            Assert.hasText(url);
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (final IOException e) {
                LOGGER.error("Error while getting site as document :{}", url, e);
                continue;
            }
            Assert.notNull(document);
            if (StringUtils.contains(url, "cpasbien")) {
                Elements el = document.select("div.ligne0 a");
                addShowLink(url, showLinks, el);
                el = document.select("div.ligne1 a");
                addShowLink(url, showLinks, el);
            } else {
                final Elements el = document.select("td a");
                addShowLink(url, showLinks, el);
            }
        }

        return showLinks;

    }

    private void addShowLink(String url, List<ShowLink> showLinks, Elements el) {
        if (el != null && !el.isEmpty()) {
            LOGGER.info("element found: {}", el.size());
            for (final Element element : el) {
                showLinks.add(new ShowLink(element.text(), element.attr("href"), url));
            }
        }
    }

    public String parseShowPage(String pageUrl) {
        String torrentUrl = null;
        LOGGER.info("parsing show page site: {}", pageUrl);
        Assert.hasText(pageUrl);
        Document document = null;
        try {
            document = Jsoup.connect(pageUrl).get();
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", pageUrl, e);
            return "";
        }
        Assert.notNull(document);
        Elements links = null;
        links = document.select("a#telecharger");
        if (links.isEmpty()) {
            links = document.select("a.download");
        }
        if (links != null && !links.isEmpty()) {
            torrentUrl = links.get(0).attr("href");
        }
        return torrentUrl;
    }

}
