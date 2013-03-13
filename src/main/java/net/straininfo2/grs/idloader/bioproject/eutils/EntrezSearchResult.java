package net.straininfo2.grs.idloader.bioproject.eutils;

/**
* Represents a result returned from Entrez Search. Identifiers can be added
 * iteratively by client code, but all parameters should be configured first.
*/
public class EntrezSearchResult {

    private int count;

    private int retMax;

    private int retStart;

    private int[] ids;

    private int idCounter;

    public EntrezSearchResult() {
        this.count = 0;
        this.retMax = 0;
        this.retStart = 0;
        this.idCounter = 0;
        this.ids = null;
    }

    public void setRetMax(int retMax) {
        this.retMax = retMax;
    }

    public void setRetStart(int retStart) {
        this.retStart = retStart;
    }

    public int getRetStart() {
        return retStart;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void addId(int id) {
        if (this.ids == null) {
            if (retMax == 0) {
                throw new RuntimeException(
                        "Error: id passed to search result but no amounts set yet");
            }
            ids = new int[Math.min(this.retMax, this.count - this.retStart)];
        }
        this.ids[idCounter++] = id;
    }

    public int[] getIds() {
        return this.ids.clone();
    }
}
