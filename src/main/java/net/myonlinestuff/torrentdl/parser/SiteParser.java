package net.myonlinestuff.torrentdl.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public interface SiteParser {

    public String getMatchingUrl();
    public Elements getElementAhref(Document document);

    public Elements getTorrentElement(Document document);
}
