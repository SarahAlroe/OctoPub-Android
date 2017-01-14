package dk.alroe.apps.octopub;

/**
 * Created by silasa on 12/14/16.
 */

public class ID {
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