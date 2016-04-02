package net.myonlinestuff.torrentdl.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ShowLink {

	private String name;
	private String pageUrl;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public ShowLink(String name, String pageUrl) {
		super();
		this.name = name;
		this.pageUrl = pageUrl;
	}
	public ShowLink() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	
}
