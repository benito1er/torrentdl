package net.myonlinestuff.torrentdl.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.myonlinestuff.torrentdl.domain.SeasonEpisode;
import net.myonlinestuff.torrentdl.domain.Show;
import net.myonlinestuff.torrentdl.domain.ShowEpisode;

@Service
public class Downloader {
	private static final Logger LOGGER = LoggerFactory.getLogger(Downloader.class);
	@Autowired
	FileManager fileManager;

	public void downloadAndStore(List<ShowEpisode> identifiedShows) {
		String name;
		int season;
		int episode;
		Assert.notNull(identifiedShows);
		for (final ShowEpisode showEpisode : identifiedShows) {
			final Show show = showEpisode.getShow();
			LOGGER.info("Processing show " + show);
			name = show.getName();
			final SeasonEpisode seasonEpisode = showEpisode.getSeasonEpisode();
			LOGGER.info("and  show seasonEpisode " + seasonEpisode);
			season = seasonEpisode != null ? seasonEpisode.getSeason() : 0;
			episode = seasonEpisode != null ? seasonEpisode.getEpisode() : RandomUtils.nextInt(0, 100);

			LOGGER.info("processing showepisode for download:{}", showEpisode);
			if (!fileManager.fileExists(name, season, episode)) {
				LOGGER.info("Show not already downloaded:{}", showEpisode);
				try {
					final URL torrent = new URL(showEpisode.getTorrentUrl());
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
					try (InputStream in = torrent.openStream()) {
						Files.copy(in, newTorrentTarget, StandardCopyOption.REPLACE_EXISTING);
						Files.copy(in, oldTorrentTarget, StandardCopyOption.REPLACE_EXISTING);
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
