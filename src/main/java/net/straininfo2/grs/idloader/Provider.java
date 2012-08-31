package net.straininfo2.grs.idloader;

import java.io.Serializable;

public final class Provider implements Serializable {
	
	private final String name;
	
	private final String abbr;
	
	private final int id;
	
	private final String url;
	
	public Provider(String name, String abbr, int id, String url) {
		this.name = name;
		this.abbr = abbr;
		this.id = id;
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAbbr() {
		return abbr;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return "Provider: " + name + "(" + abbr + ")" + " - id " + id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if (o instanceof Provider) {
			Provider other = (Provider)o;
			return this.id == other.id;
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
}