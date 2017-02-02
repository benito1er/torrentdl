package net.myonlinestuff.torrentdl.parser;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class AbstractSiteParser implements SiteParser {
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
}
