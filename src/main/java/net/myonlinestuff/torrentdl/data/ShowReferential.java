package net.myonlinestuff.torrentdl.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import net.myonlinestuff.torrentdl.domain.Show;
import net.myonlinestuff.torrentdl.domain.ShowEpisode;

@Component
public class ShowReferential {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowReferential.class);
	// private static final String SHOW_PROPERTIES_FILE =
	// "shows.properties.file";
	private static final String SHOW_PROPERTIES = "shows.properties";

	@Autowired
	Environment env;

	Map<String, Show> shows = new HashMap<>();
	List<ShowEpisode> showEpisodes = new ArrayList<>();

	@PostConstruct
	public void init() {
		String resource = this.getClass().getClassLoader().getResource(SHOW_PROPERTIES).getFile();

		try (Scanner scanner = new Scanner(new File(resource))) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (StringUtils.startsWith(line, "#"))
					continue;
				if(StringUtils.strip(line).length()<1)
					continue;
				String[] tokens = line.split("\\|");
				String code = tokens.length > 0 ? StringUtils.strip(tokens[0]) : null;
				String name = tokens.length > 1 ? StringUtils.strip(tokens[1]) : StringUtils.strip(StringUtils.substringBefore(code, "#"));
				if(StringUtils.contains(code, "#")){
					code = StringUtils.replace(code, "#", ".");
				}
				if(!StringUtils.endsWith(code, "*")){
					code = code+"."+"vostfr";
				}else{
					code = StringUtils.replace(code, "*", "");
					name= StringUtils.replace(name, "*", "");
				}
				code = StringUtils.upperCase(StringUtils.replace(code, ".", " "));
				Show show = new Show(name, code);
				shows.put(code, show);
			}
			scanner.close();

		} catch (IOException e) {
			LOGGER.error("Error while loading show list", e);
		}
	}

	public Map<String, Show> getShows() {
		return shows;
	}

	public void setShows(Map<String, Show> shows) {
		this.shows = shows;
	}

	public List<ShowEpisode> getShowEpisodes() {
		return showEpisodes;
	}

	public void setShowEpisodes(List<ShowEpisode> showEpisodes) {
		this.showEpisodes = showEpisodes;
	}

}
