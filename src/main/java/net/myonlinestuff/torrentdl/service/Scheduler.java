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
	
	

//	@Scheduled(fixedRate = 3600000)
//	public void downloadLastestShowTorrent() {
//        String url = env.getProperty("site.url");
//        identifierAndDownload(Arrays.asList(url));
//
//    }

    @Scheduled(fixedRate = 30801000)
	public void downloadLookingForShowTorrent() {
		String[] searchRootUrls = StringUtils.split(env.getProperty("site.url.search"),",");
		for (String searchRootUrl : searchRootUrls) {
			final Map<String, String> lookingForEachTvShow = getPageByResearch(searchRootUrl);
			identifierAndDownload(lookingForEachTvShow);
		}
	}

    private void identifierAndDownload(Map<String, String> urls) {
        final Map<String, List<ShowLink>> lookingForShowLinks = new HashMap<>();
        for (final Map.Entry<String, String> urlForLookingForEntry : urls.entrySet()) {
            lookingForShowLinks.put(urlForLookingForEntry.getKey(), parser.parseRootPage(urlForLookingForEntry.getValue()));
        }
        final List<ShowEpisode> lookingForIdentifiedShows = identifyShows(lookingForShowLinks);
        LOGGER.info("Found : " + lookingForIdentifiedShows.size() + " / " + showReferential.getShows().size());
        downloader.downloadAndStore(lookingForIdentifiedShows);
    }
	

    private List<ShowEpisode> identifyShows(Map<String, List<ShowLink>> showLinks) {
        final List<ShowEpisode> identifiedShows = new ArrayList<>();
        String[] urlRoots = StringUtils.split(env.getProperty("site.url.root"),",");
        for (final Map.Entry<String, List<ShowLink>> showLinkEntry : showLinks.entrySet()) {
            final String originalName = showLinkEntry.getKey();
            for (final ShowLink showLink : showLinkEntry.getValue()) {
            	String urlRoot = null;
            	for(String urlR : urlRoots){
            		if(StringUtils.containsIgnoreCase(showLink.getUrlRoot(), urlR)){
            			urlRoot = urlR;
            			break;
            		}
            	}
                final ShowEpisode identifiedShow = showIdentifier.identify(originalName, showLink.getName());
                if (identifiedShow != null) {
                    final String showTorrentUrl = parser.parseShowPage(urlRoot +showLink.getPageUrl());
                    if (StringUtils.isNotBlank(showTorrentUrl)) {
                        
						identifiedShow.setTorrentUrl( urlRoot +showTorrentUrl);
                        identifiedShows.add(identifiedShow);
                    }
                }
            }
        }
        return identifiedShows;
    }

    private Map<String, String> getPageByResearch(String searchRootUrl) {
        final Set<String> tempLookingForShows = showReferential.getShows().keySet();
        final Set<String> lookingForShows = new TreeSet<>();
        for (final String tolower : tempLookingForShows) {
            lookingForShows.add(StringUtils.lowerCase(tolower));
        }
        final Map<String, String> results = new HashMap<>();
        for (final String looking : lookingForShows) {
            final StringBuilder sb = new StringBuilder();
            sb.append(searchRootUrl).append(StringUtils.replace(looking, ".", "")).append(".html");
            final String lookingForUrl = sb.toString();
            results.put(looking, lookingForUrl);
        }
        return results;
    }
}
