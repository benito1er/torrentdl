package net.myonlinestuff.torrentdl.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SeedpeerSiteParser extends AbstractSiteParser {

    @Override
    public Elements getElementAhref(Document document) {
        final Elements result = new Elements();
        final Elements trClasstDark = document.select("div#body");
        for (final Element element : trClasstDark) {
            result.addAll(element.select("a[href]"));
        }
        return result;// document.select("center tbody a[href]");
    }

    @Override
    public Elements getTorrentElement(Document document) {
        final Elements result = new Elements();
        result.addAll(document.select("div.downloadTorrent a[href*=magnet:]"));
        result.addAll(document.select("div.downloadTorrent a[rel]"));
        return result;
    }
}
