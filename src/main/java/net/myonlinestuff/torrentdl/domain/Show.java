package net.myonlinestuff.torrentdl.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Show implements Comparable<Show> {

	private String name;
	private String code;
	private String realName;
	public String getName() {
        return StringUtils.defaultIfBlank(realName, name);
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Show(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	public Show() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public int compareTo(Show o) {
		if(o ==null ) {
			throw new IllegalArgumentException();
		}else {
			final Show other = o;
			final Integer o1Size = other.getName().length();
			final Integer o2Size = this.getName().length();
			return o1Size.compareTo(o2Size);
		}
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

    public void setRealName(String realName) {
        this.realName = realName;

    }
	
	
}
