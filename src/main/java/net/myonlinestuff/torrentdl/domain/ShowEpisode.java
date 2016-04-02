package net.myonlinestuff.torrentdl.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ShowEpisode {

	private Show show;
	private String torrentUrl;
	private SeasonEpisode seasonEpisode;
	private boolean downloaded;
	
	public String getTorrentUrl() {
		return torrentUrl;
	}
	public void setTorrentUrl(String torrentUrl) {
		this.torrentUrl = torrentUrl;
	}
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
	
	
	
	
		
	
}
