package dk.alroe.apps.octopub;

import android.content.Context;
import android.util.AttributeSet;

import org.markdownj.MarkdownProcessor;
import org.markdownj.TextEditor;

import us.feras.mdv.MarkdownView;

/**
 * Created by silasa on 1/25/17.
 */

public class MarkdownViewRework extends MarkdownView {
    public MarkdownViewRework(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkdownViewRework(Context context) {
        super(context);
    }

    public void loadMarkdown(String txt, String cssFileUrl) {
        loadMarkdownToView(txt, cssFileUrl);
    }

    private void loadMarkdownToView(String txt, String cssFileUrl) {
        MarkdownProcessor m = new MarkdownProcessor();
        String preHTML = preProcess(txt);
        String html = m.markdown(preHTML);
        if (cssFileUrl != null) {
            html = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><link rel='stylesheet' type='text/css' href='" + cssFileUrl + "' /><div style='word-wrap:break-word;'>" + html + "</div>";
        }
        loadDataWithBaseURL("fake://", html, "text/html", "UTF-8", null);
    }

    private String preProcess(String txt) {
        TextEditor textEdit = new TextEditor(txt);
        textEdit.replaceAll("!\\[g\\]\\((.*)\\.webm\\)", "<video autoplay muted loop src=\"$1\\.mp4\" />");
        textEdit.replaceAll("!\\[g\\]\\((.*)\\.mp4\\)", "<video autoplay muted loop src=\"$1\\.mp4\" />");
        textEdit.replaceAll("!\\[(.*)\\]\\((.*)\\.webm\\)", "<video controls src=\"$2\\.mp4\" alt=\"$1\" />");
        textEdit.replaceAll("!\\[(.*)\\]\\((.*)\\.mp4\\)", "<video controls src=\"$2\\.mp4\" alt=\"$1\" />");
        textEdit.replaceAll("!\\[(.*)\\]\\((.*)\\.mp3\\)", "<audio controls src=\"$2\\.mp3\" alt=\"$1\" />");
        textEdit.replaceAll("!\\[(.*)\\]\\((.*)\\.wav\\)", "<audio controls src=\"$2\\.wav\" alt=\"$1\" />");
        textEdit.replaceAll("!\\[(.*)\\]\\((.*)\\.ogg\\)", "<audio controls src=\"$2\\.ogg\" alt=\"$1\" />");
        textEdit.replaceAll("!\\[.*\\]\\(.*youtube\\.com.*?v=(.*)\\)", "<iframe width='100%' height='200px' src='http://www.youtube.com/embed/$1' allowfullscreen></iframe>");
        textEdit.replaceAll("!\\[.*\\]\\(.*youtu\\.be/(.*)\\)", "<iframe width='100%' height='200px' src='http://www.youtube.com/embed/$1' allowfullscreen></iframe>");
        return textEdit.toString();
    }
}
