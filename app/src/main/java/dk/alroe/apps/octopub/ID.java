package dk.alroe.apps.octopub;

/**
 * Created by silasa on 12/14/16.
 */

public class ID { //TODO tip to improve readability use a more specific name for this class.
    //TODO tip. Abbreviations should be written with capital first letter. Rest should be small in order to improve readability.  AnIDIDontKnow is hard to read AnIdIDontKnow is easier to read.
    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    private final String id;
    private final String hash;

    public ID(String id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "ID: "+id+", hash: "+hash;
    }
}