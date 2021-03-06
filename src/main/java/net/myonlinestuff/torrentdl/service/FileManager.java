package net.myonlinestuff.torrentdl.service;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FileManager {
    private static final String TORRENT_ROOT_FOLDER = "torrent.root.folder";
    private static final String OLD_FOLDER = "torrent.root.folder.old";
    private static final String NEW_FOLDER = "torrent.root.folder.new";
    //
    @Autowired
    Environment env;

    public boolean fileExists(String code, int season, int episode) {
        final boolean found = false;
        final String folderName = buildEpisodeFolderName(code, season, episode, true);

        final String fileName = buildEpisodeFileName(code, season, episode);
        try {
        final Path folderAsPath = Paths.get(folderName, fileName);
        if (Files.exists(folderAsPath)) {
            return true;
        }
        } catch (final InvalidPathException e) {
            return true;
        }

        return found;
    }

    public String buildEpisodeFolderName(String code, int season, int episode, boolean isOld) {
        final String torrentRootFolder = isOld ? getOldFolder() : getNewFolder();
        final String episodeFolderName = String.format("%s\\%s\\s%d\\e%d", torrentRootFolder, code, season, episode);
        return episodeFolderName;
    }

    public String buildEpisodeFileName(String code, int season, int episode) {
        final String episodeFileName = String.format("%s_s%d_e%d.torrent", code, season, episode);
        return episodeFileName;
    }

    public String getOldFolder() {
        return new StringBuilder().append(env.getProperty(TORRENT_ROOT_FOLDER)).append(env.getProperty(OLD_FOLDER)).toString();
    }

    public String getNewFolder() {
        return new StringBuilder().append(env.getProperty(TORRENT_ROOT_FOLDER)).append(env.getProperty(NEW_FOLDER)).toString();
    }
}
