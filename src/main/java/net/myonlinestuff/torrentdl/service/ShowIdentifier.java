package net.myonlinestuff.torrentdl.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import net.myonlinestuff.torrentdl.data.ShowReferential;
import net.myonlinestuff.torrentdl.domain.SeasonEpisode;
import net.myonlinestuff.torrentdl.domain.Show;
import net.myonlinestuff.torrentdl.domain.ShowEpisode;

@Component
public class ShowIdentifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowIdentifier.class);
	Pattern normalPattern = Pattern.compile("s(\\d{1,2})e(\\d{1,2})");
	

	@Autowired
	ShowReferential referential ;

	public ShowEpisode identify(String name) {
		Assert.notNull(name);
		LOGGER.info("trying to identified show from name:{}]",name);
		ShowEpisode identifiedShow = null;
		String nameToSearch= name.toLowerCase();
		
		List<Show> candidateShow = getCandidateShow(nameToSearch);
		if(!candidateShow.isEmpty()) {
			Collections.sort(candidateShow);
			identifiedShow = new ShowEpisode();
			identifiedShow.setShow(candidateShow.get(0));
			LOGGER.info("identified show:{}",identifiedShow.getShow().getName());
			SeasonEpisode seasonEpisode = findSeasonEpisode(nameToSearch);
			identifiedShow.setSeasonEpisode(seasonEpisode);
		}
		
		return identifiedShow;
	}
	
	
	
	private SeasonEpisode findSeasonEpisode(String nameToSearch) {
		Assert.notNull(nameToSearch);
        LOGGER.debug("Find season and episode in:{}", nameToSearch);
		SeasonEpisode se = null;
		Matcher matcher = normalPattern.matcher(nameToSearch);
		
		if(matcher.find() && matcher.groupCount() >=2) {
			se = new SeasonEpisode();
			se.setSeason(Integer.parseInt(matcher.group(1),10));
			se.setEpisode(Integer.parseInt(matcher.group(2),10));
			
        } else {
            String[] showNameAsArray = StringUtils.split(nameToSearch);
            for (String token : showNameAsArray) {
                Integer sAndE = null;
                try {
                    sAndE = Integer.parseInt(token);
                } catch (NumberFormatException e) {
                    LOGGER.debug("Not the Saison and the Episode " + token);
                }
                if (sAndE != null) {
                    se = new SeasonEpisode();
                    se.setSeason(0);
                    se.setEpisode(sAndE);
                    break;
                }
            }
		}
		return se;
	}
	
	private List<Show> getCandidateShow(final String nameToSearch ){
		List<Show> candidates = new ArrayList<>();
		StartWithPredicate swp = new StartWithPredicate(nameToSearch);
		Collection<Show> showNames = referential.getShows().values();
		Collection<Show> filtered = Collections2.filter(showNames,swp);
		candidates.addAll(filtered);
		return candidates;
	}
		
	
//	private class StringLenghtComparator implements Comparator<String>{
//
//		@Override
//		public int compare(String o1, String o2) {
//			if(o1 ==null || o2 ==null) {
//				throw new IllegalArgumentException();
//			}else {
//				Integer o1Size = o1.length();
//				Integer o2Size = o2.length();
//				return o1Size.compareTo(o2Size);
//			}
//			
//		}
//		
//	}
	private class StartWithPredicate implements Predicate<Show> {

		private final String nameToSearch;
		
		public StartWithPredicate(String nameToSearch) {
			super();
			this.nameToSearch = nameToSearch;
		}

		@Override
		public boolean apply(Show input) {
            String name = input.getName();
            name = StringUtils.substringBefore(StringUtils.replace(StringUtils.lowerCase(name), ".", " "),"#");
            String tempNameToSearch = StringUtils.replace(StringUtils.lowerCase(nameToSearch), ".", " ");
            LOGGER.debug("StartWithPredicate for nameToSearch : " + tempNameToSearch + " and name formated " + name);
            boolean startsWith = tempNameToSearch.startsWith(name);
            if(!startsWith)
            	LOGGER.debug("Nothing foound for nameToSearch : " + tempNameToSearch + " and name formated " + name);
			return startsWith;
		}
		
	}
}
