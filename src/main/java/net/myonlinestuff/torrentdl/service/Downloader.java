package net.myonlinestuff.torrentdl.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.myonlinestuff.torrentdl.domain.SeasonEpisode;
import net.myonlinestuff.torrentdl.domain.Show;
import net.myonlinestuff.torrentdl.domain.ShowEpisode;
import net.myonlinestuff.torrentdl.parser.SiteParser;
import net.myonlinestuff.torrentdl.parser.Torrent9SiteParser;

@Service
public class Downloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(Downloader.class);
    @Autowired
    FileManager fileManager;

    @Autowired
    private List<SiteParser> siteParsers;

    private final Map<String, SiteParser> siteParserMap = new HashMap<>();

    public void downloadAndStore(List<ShowEpisode> identifiedShows) {
        String name;
        int season;
        int episode;
        Assert.notNull(identifiedShows);
        for (final ShowEpisode showEpisode : identifiedShows) {
            final Show show = showEpisode.getShow();
            LOGGER.info("Processing show " + show);
            name = show.getName();
            final String[] names = StringUtils.split(name, ".");
            final StringBuilder sb = new StringBuilder();
            for (final String temp : names) {
                sb.append(StringUtils.capitalize(temp));
                if (!StringUtils.equalsIgnoreCase(names[names.length - 1], temp)) {
                    sb.append(".");
                }
            }
            name = sb.toString();
            final SeasonEpisode seasonEpisode = showEpisode.getSeasonEpisode();
            LOGGER.info("and  show seasonEpisode " + seasonEpisode);
            season = seasonEpisode != null ? seasonEpisode.getSeason() : 0;
            episode = seasonEpisode != null ? seasonEpisode.getEpisode() : RandomUtils.nextInt(0, 100);

            LOGGER.info("processing showepisode for download:{}", showEpisode);
            if (!fileManager.fileExists(name, season, episode)) {
                LOGGER.info("Show not already downloaded:{}", showEpisode);
                try {

                    final String newTorrentFolderName = fileManager.buildEpisodeFolderName(name, season, episode, false);
                    final Path newFolderPath = Paths.get(newTorrentFolderName);
                    if (!Files.exists(newFolderPath)) {
                        Files.createDirectories(newFolderPath);
                    }
                    final String oldTorrentFolderName = fileManager.buildEpisodeFolderName(name, season, episode, true);
                    final Path oldFolderPath = Paths.get(oldTorrentFolderName);
                    if (!Files.exists(oldFolderPath)) {
                        Files.createDirectories(oldFolderPath);
                    }
                    final String torrentFileName = fileManager.buildEpisodeFileName(name, season, episode);
                    final Path newTorrentTarget = Paths.get(newTorrentFolderName, torrentFileName);
                    final Path oldTorrentTarget = Paths.get(oldTorrentFolderName, torrentFileName);
                    if (Files.exists(oldTorrentTarget)) {
                        continue;
                    }
                    InputStream newTorrentIn = null;
                    try {
                        for (final String torrentUrl : showEpisode.getTorrentUrls()) {

                            final String urlRoot = StringUtils.substringBefore(StringUtils.replace(torrentUrl, "http://", ""), "/");
                            SiteParser urlSiteparser = siteParserMap.get(urlRoot);
                            if (urlSiteparser == null) {
                                urlSiteparser = SiteParserUtils.getSiteParserByUrl(siteParsers, torrentUrl);
                                if (urlSiteparser != null) {
                                    urlSiteparser.setUrlRoot("http://" + urlRoot);
                                    urlSiteparser.initCoockies();
                                }
                                siteParserMap.put(urlRoot, urlSiteparser);
                            }
                            final URL torrentURL = new URL(torrentUrl);
                            final URLConnection conn = torrentURL.openConnection();
                            if (StringUtils.isNotBlank(urlSiteparser.getCoockiesForUrlConn())) {
                                conn.setRequestProperty("Cookie", urlSiteparser.getCoockiesForUrlConn());
                                conn.setRequestProperty("User-Agent", Torrent9SiteParser.USER_AGENT);
                            }
                            newTorrentIn = conn.getInputStream();

                            Files.copy(newTorrentIn, newTorrentTarget, StandardCopyOption.REPLACE_EXISTING);
                            final String torrentUrlValue = FileUtils.readFileToString(newTorrentTarget.toFile());
                            if (StringUtils.startsWithIgnoreCase(torrentUrlValue, "\n<!DOCTYPE") || StringUtils.startsWithIgnoreCase(torrentUrlValue, "<html>")) {
                                FileUtils.forceDelete(newTorrentTarget.toFile());
                                continue;
                            }
                            Files.copy(newTorrentTarget, oldTorrentTarget, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } finally {
                        org.apache.commons.io.IOUtils.closeQuietly(newTorrentIn);
                    }
                    LOGGER.info("Show downloaded:{}", showEpisode);

                } catch (final IOException e) {
                    LOGGER.error("Error while downloading", e);
                }
            }
        }
        LOGGER.info("All Shows have been  downloaded:{}");
        // then upload
    }

}
