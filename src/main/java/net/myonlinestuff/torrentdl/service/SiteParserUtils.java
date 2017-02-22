package net.myonlinestuff.torrentdl.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.myonlinestuff.torrentdl.parser.SiteParser;

public class SiteParserUtils {

    public static SiteParser getSiteParserByUrl(List<SiteParser> siteParsers, String url) {
        for (final SiteParser siteParser : siteParsers) {
            final String specificClassName = siteParser.getMatchingUrl();
            if (StringUtils.containsIgnoreCase(url, specificClassName)) {
                return siteParser;
            }
        }
        return null;
    }

}
