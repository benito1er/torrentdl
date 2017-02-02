package net.myonlinestuff.torrentdl.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class CpasbienSiteParser extends AbstractSiteParser {

    @Override
    public Elements getElementAhref(org.jsoup.nodes.Document document) {
        return document.select("div.ligne0 a");
    }

    @Override
    public Elements getTorrentElement(Document document) {
        return document.select("a#telecharger");
    }

}
