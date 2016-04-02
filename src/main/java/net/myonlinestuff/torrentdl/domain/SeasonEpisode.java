package net.myonlinestuff.torrentdl.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SeasonEpisode {
	private Integer season;
	private Integer episode;
	public Integer getSeason() {
		return season;
	}
	public void setSeason(Integer season) {
		this.season = season;
	}
	public Integer getEpisode() {
		return episode;
	}
	public void setEpisode(Integer episode) {
		this.episode = episode;
	}
	public SeasonEpisode(Integer season, Integer episode) {
		super();
		this.season = season;
		this.episode = episode;
	}
	public SeasonEpisode() {
		super();
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	
}
