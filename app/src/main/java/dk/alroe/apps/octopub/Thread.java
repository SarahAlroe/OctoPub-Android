package dk.alroe.apps.octopub;

/**
 * Created by silasa on 12/10/16.
 */

public class Thread {
    private String title;
    private String id;
    private int length;
    private String text;


    public Thread(String title, String id, int length) {
        this.title = title;
        this.id = id;
        this.length = length;
    }

    public Thread(String title, String id, int length, String text) {
        this.title = title;
        this.id = id;
        this.length = length;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
