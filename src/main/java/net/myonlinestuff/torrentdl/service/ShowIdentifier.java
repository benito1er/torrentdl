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
    ShowReferential referential;

    public ShowEpisode identify(String code, String showLinkName) {
        Assert.notNull(showLinkName);
        LOGGER.info("trying to identified show from name:{}]", showLinkName);
        ShowEpisode identifiedShow = null;
        final String nameToSearch = showLinkName.toLowerCase();

        final List<Show> candidateShow = getCandidateShow(code, nameToSearch);
        if (!candidateShow.isEmpty()) {
            Collections.sort(candidateShow);
            identifiedShow = new ShowEpisode();
            identifiedShow.setShow(candidateShow.get(0));
            LOGGER.info("identified show:{}", identifiedShow.getShow().getName());
            final SeasonEpisode seasonEpisode = findSeasonEpisode(nameToSearch);
            identifiedShow.setSeasonEpisode(seasonEpisode);
        }

        return identifiedShow;
    }

    private SeasonEpisode findSeasonEpisode(String nameToSearch) {
        Assert.notNull(nameToSearch);
        LOGGER.debug("Find season and episode in:{}", nameToSearch);
        SeasonEpisode se = null;
        final Matcher matcher = normalPattern.matcher(nameToSearch);

        if (matcher.find() && matcher.groupCount() >= 2) {
            se = new SeasonEpisode();
            se.setSeason(Integer.parseInt(matcher.group(1), 10));
            se.setEpisode(Integer.parseInt(matcher.group(2), 10));

        } else {
            final String[] showNameAsArray = StringUtils.split(nameToSearch);
            for (final String token : showNameAsArray) {
                Integer sAndE = null;
                try {
                    sAndE = Integer.parseInt(token);
                } catch (final NumberFormatException e) {
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

    private List<Show> getCandidateShow(String code, final String nameToSearch) {
        final List<Show> candidates = new ArrayList<>();
        final StartWithPredicate swp = new StartWithPredicate(nameToSearch);
        final List<Show> currentShows = referential.getShows().get(StringUtils.upperCase(code));
        for (final Show currentShow : currentShows) {
            final Collection<Show> showNames;
            showNames = new ArrayList<>();
            if (currentShow != null) {
                showNames.add(currentShow);
            } else {
                final Collection<List<Show>> values = referential.getShows().values();
                for (final List<Show> v : values)
                    showNames.addAll(v);
            }
            final Collection<Show> filtered = Collections2.filter(showNames, swp);
            candidates.addAll(filtered);
        }
        return candidates;
    }

    private class StartWithPredicate implements Predicate<Show> {

        private final String nameToSearch;

        public StartWithPredicate(String nameToSearch) {
            super();
            this.nameToSearch = nameToSearch;
        }

        @Override
        public boolean apply(Show input) {
            String name = input.getName();
            name = StringUtils.substringBefore(StringUtils.replace(StringUtils.lowerCase(name), ".", " "), "#");
            final String tempNameToSearch = StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.lowerCase(nameToSearch), ".", " "), "(", ""), ")", "");
            LOGGER.debug("StartWithPredicate for nameToSearch : " + tempNameToSearch + " and name formated " + name);
            String season;
            final String lowerFileName = StringUtils.lowerCase(tempNameToSearch);
            final Matcher normalMatcher = normalPattern.matcher(lowerFileName);
            final boolean normalMGroup = normalMatcher.groupCount() >= 2;
            final boolean normalFinder = normalMatcher.find();
            String realName = null;
            if (normalFinder && normalMGroup) {
                normalMatcher.group();
                season = normalMatcher.group(1);
                normalMatcher.group(2);
                realName = StringUtils.replace(StringUtils.substringBefore(lowerFileName, "s" + season), ":", " ");
                if (realName.endsWith(".")) {
                    realName = StringUtils.removeEnd(realName, ".");
                }
                realName = StringUtils.replace(StringUtils.capitalize(StringUtils.strip(realName)), " ", ".");

                realName = StringUtils.replace(realName, "..", ".");

            }

            boolean startsWith = tempNameToSearch.startsWith(name);
            if (!startsWith) {
                final int indefOf = StringUtils.indexOfIgnoreCase(tempNameToSearch, name);
                final String subStringTempNameToSearch = StringUtils.substring(tempNameToSearch, 0, (indefOf > 0 ? indefOf : 0) + name.length());
                final int fuzzyLogic = StringFuzyy.fuzzyLogic(name, subStringTempNameToSearch) + (indefOf < 0 ? -20 : 0);
                final boolean goodScore = fuzzyLogic > 80;
                startsWith = goodScore || StringUtils.containsIgnoreCase(tempNameToSearch, " " + name + " ");
            }
            if (!startsWith) {
                LOGGER.info("Nothing foound for nameToSearch : " + tempNameToSearch + " and name formated " + name);
            } else {
                if (realName != null && realName.length() > 1.3 * name.length()) {
                    input.setRealName(realName);
                }
            }
            return startsWith;
        }

    }
}
