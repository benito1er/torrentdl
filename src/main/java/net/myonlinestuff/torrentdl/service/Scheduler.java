package net.myonlinestuff.torrentdl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.myonlinestuff.torrentdl.data.ShowReferential;
import net.myonlinestuff.torrentdl.domain.ShowEpisode;
import net.myonlinestuff.torrentdl.domain.ShowLink;

@Component
public class Scheduler {
    private static final String URL_SEPARATOR = "\t";

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    Parser parser;

    @Autowired
    Downloader downloader;

    @Autowired
    ShowIdentifier showIdentifier;

    @Autowired
    Environment env;

    @Autowired
    ShowReferential showReferential;

    // @Scheduled(fixedRate = 3600000)
    // public void downloadLastestShowTorrent() {
    // String url = env.getProperty("site.url");
    // identifierAndDownload(Arrays.asList(url));
    //
    // }

    @Scheduled(fixedRate = 30801000)
    public void downloadLookingForShowTorrent() {
        final String[] searchRootUrls = StringUtils.split(env.getProperty("site.url.search"), ",");
        final Map<String, String> lookingForEachTvShow = getPageByResearch(searchRootUrls);
        identifierAndDownload(lookingForEachTvShow);
    }

    private void identifierAndDownload(Map<String, String> urls) {
        final Map<String, List<ShowLink>> lookingForShowLinks = new HashMap<>();
        for (final Map.Entry<String, String> urlForLookingForEntry : urls.entrySet()) {
            lookingForShowLinks.put(urlForLookingForEntry.getKey(), parser.parseRootPage(StringUtils.split(urlForLookingForEntry.getValue(), URL_SEPARATOR)));
        }
        final List<ShowEpisode> lookingForIdentifiedShows = identifyShows(lookingForShowLinks);
        LOGGER.info("Found : " + lookingForIdentifiedShows.size() + " / " + showReferential.getShows().size());
        downloader.downloadAndStore(lookingForIdentifiedShows);
    }

    private List<ShowEpisode> identifyShows(Map<String, List<ShowLink>> showLinks) {
        final List<ShowEpisode> identifiedShows = new ArrayList<>();
        final String[] urlRoots = StringUtils.split(env.getProperty("site.url.root"), ",");
        for (final Map.Entry<String, List<ShowLink>> showLinkEntry : showLinks.entrySet()) {
            final String originalName = showLinkEntry.getKey();
            for (final ShowLink showLink : showLinkEntry.getValue()) {
                if (StringUtils.isBlank(showLink.getPageUrl())) {
                    continue;
                }
                String urlRoot = null;
                for (final String urlR : urlRoots) {
                    if (StringUtils.containsIgnoreCase(showLink.getUrlRoot(), urlR)) {
                        urlRoot = urlR;
                        break;
                    }
                }
                final ShowEpisode identifiedShow = showIdentifier.identify(originalName, showLink.getName());
                if (identifiedShow != null) {

                    final String torrentUrl = StringUtils.startsWith(showLink.getPageUrl(), "http:") ? showLink.getPageUrl()
                            : StringUtils.startsWith(showLink.getPageUrl(), "/") && StringUtils.endsWith(urlRoot, "/") ? StringUtils.substringBeforeLast(urlRoot, "/") + showLink.getPageUrl()
                                    : urlRoot + showLink.getPageUrl();
                    final List<String> showTorrentUrls = parser.parseShowPage(torrentUrl);
                    for (final String showTorrentUrl : showTorrentUrls) {
                        if (StringUtils.isNotBlank(showTorrentUrl)) {
                            identifiedShow.addTorrentUrl(StringUtils.startsWith(showTorrentUrl, "/") && StringUtils.endsWith(urlRoot, "/")
                                    ? StringUtils.substringBeforeLast(urlRoot, "/") + showTorrentUrl : urlRoot + showTorrentUrl);
                        }
                    }
                    identifiedShows.add(identifiedShow);
                }
            }
        }
        return identifiedShows;
    }

    private Map<String, String> getPageByResearch(String[] searchRootUrls) {
        final Set<String> tempLookingForShows = showReferential.getShows().keySet();
        final Set<String> lookingForShows = new TreeSet<>();
        for (final String tolower : tempLookingForShows) {
            lookingForShows.add(StringUtils.lowerCase(tolower));
        }
        final Map<String, String> results = new HashMap<>();
        for (final String looking : lookingForShows) {
            final StringBuilder urlSiteBuilder = new StringBuilder();
            for (final String searchRootUrl : searchRootUrls) {
                final StringBuilder sb = new StringBuilder();
                if (!StringUtils.containsIgnoreCase(searchRootUrl, "seedpeer")) {
                    sb.append(searchRootUrl).append(StringUtils.replace(StringUtils.replace(looking, " ", "%20"), ".", "")).append(".html");
                } else {
                    sb.append(searchRootUrl).append(StringUtils.replace(StringUtils.replace(looking, " ", "+"), ".", ""));
                }
                final String lookingForUrl = sb.toString();
                urlSiteBuilder.append(lookingForUrl).append(URL_SEPARATOR);
            }
            results.put(looking, urlSiteBuilder.toString());
        }
        return results;
    }
}
