package net.myonlinestuff.torrentdl.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ShowEpisode {

	private Show show;
	private SeasonEpisode seasonEpisode;
	private boolean downloaded;
    private final List<String> torrentUrls = new ArrayList<>();
	
	public SeasonEpisode getSeasonEpisode() {
		return seasonEpisode;
	}
	public void setSeasonEpisode(SeasonEpisode seasonEpisode) {
		this.seasonEpisode = seasonEpisode;
	}
	public Show getShow() {
		return show;
	}
	public void setShow(Show show) {
		this.show = show;
	}
	
	
		
	public boolean isDownloaded() {
		return downloaded;
	}
	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}
	public String getLibelle() {
		return getShow().getName()+ " " +getSeasonEpisode().toString();
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

    public void addTorrentUrl(String torrentUrl) {
        torrentUrls.add(torrentUrl);

    }

    public List<String> getTorrentUrls() {
        return torrentUrls;
    }
	
	
	
	
		
	
}
