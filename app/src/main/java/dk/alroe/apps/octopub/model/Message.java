package dk.alroe.apps.octopub.model;

/**
 * Created by silasa on 12/14/16.
 */

public class Message {
    private final String text;
    private final String id;
    private final int time;
    private final int number;

    public Message(String text, String id, int time, int number) {
        this.text = text;
        this.id = id;
        this.time = time;
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public int getNumber() {
        return number;
    }
}
