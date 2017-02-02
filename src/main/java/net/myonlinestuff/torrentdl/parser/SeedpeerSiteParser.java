package net.myonlinestuff.torrentdl.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SeedpeerSiteParser extends AbstractSiteParser {

    @Override
    public Elements getTorrentElement(Document document) {
        return document.select("a#rel");
    }
}
