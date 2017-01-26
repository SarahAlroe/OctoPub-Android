package dk.alroe.apps.octopub.model;

/**
 * Created by silasa on 12/14/16.
 */

public class UserId {

    private final String id;
    private final String hash;

    public UserId(String id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "UserId: " + id + ", hash: " + hash;
    }
}