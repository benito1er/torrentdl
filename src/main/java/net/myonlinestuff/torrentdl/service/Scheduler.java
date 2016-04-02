package net.myonlinestuff.torrentdl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Scheduled(fixedRate = 82801000)
    public void downloadLookingForShowTorrent() {
        List<String> lookingForEachTvShow = getPageByResearch();
        identifierAndDownload(lookingForEachTvShow);
    }

    private void identifierAndDownload(List<String> urls) {
        List<ShowLink> lookingForShowLinks = new ArrayList<>();
        for (String urlForLookingFor : urls) {
            lookingForShowLinks.addAll(parser.parseRootPage(urlForLookingFor));
        }
        List<ShowEpisode> lookingForIdentifiedShows = identifyShows(lookingForShowLinks);
        LOGGER.info("Found : " + lookingForIdentifiedShows.size() + " / " + showReferential.getShows().size());
        downloader.downloadAndStore(lookingForIdentifiedShows);
    }
	

	private List<ShowEpisode> identifyShows(List<ShowLink> showLinks){
		List<ShowEpisode> identifiedShows = new ArrayList<>();
		for (ShowLink showLink : showLinks) {
			ShowEpisode identifiedShow = showIdentifier.identify(showLink.getName());
			if (identifiedShow != null) {
				String showTorrentUrl = parser.parseShowPage(showLink.getPageUrl());
                if (StringUtils.isNotBlank(showTorrentUrl)) {
					identifiedShow.setTorrentUrl(env.getProperty("site.url.root")+ showTorrentUrl);
					identifiedShows.add(identifiedShow);
				}
			}
		}
		return identifiedShows;
	}

    private List<String> getPageByResearch() {
        Set<String> tempLookingForShows = showReferential.getShows().keySet();
        Set<String> lookingForShows = new TreeSet<>();
        for (String tolower : tempLookingForShows) {
            lookingForShows.add(StringUtils.lowerCase(tolower));
        }
        List<String> results = new ArrayList<>();
        String searchRootUrl = env.getProperty("site.url.search");
        for (String looking : lookingForShows) {
            StringBuilder sb = new StringBuilder();
            sb.append(searchRootUrl).append(StringUtils.replace(looking, ".", "")).append(".html");
            String lookingForUrl = sb.toString();
            results.add(lookingForUrl);
        }
        return results;
    }
}
