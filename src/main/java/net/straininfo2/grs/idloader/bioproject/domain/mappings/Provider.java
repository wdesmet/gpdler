package net.straininfo2.grs.idloader.bioproject.domain.mappings;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Provider implements Serializable {
	
	private String name;
	
	private String abbr;
	
	private long id;
	
	private String url;

    private Set<Mapping> mappings;
	
	public Provider(String name, String abbr, int id, String url) {
		this.setName(name);
		this.setAbbr(abbr);
		this.setId(id);
		this.setUrl(url);
	}

    public Provider() {

    }
	
	public String getName() {
		return name;
	}

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
		return abbr;
	}

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Id
    public long getId() {
		return id;
	}

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
		return url;
	}

    public void setUrl(String url) {
        this.url = url;
    }

    // if you remove a provider, also remove mappings associated with it
    // what you can do is remove all mappings associated with a provider,
    // that provider will continue to exist.
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    public Set<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<Mapping> mappings) {
        this.mappings = mappings;
    }

    @Override
	public String toString() {
		return "Provider: " + getName() + "(" + getAbbr() + ")" + " - id " + getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if (o instanceof Provider) {
			Provider other = (Provider)o;
			return this.getId() == other.getId();
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int) this.getId();
	}
}