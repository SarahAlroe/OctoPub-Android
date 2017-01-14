package dk.alroe.apps.octopub;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import us.feras.mdv.MarkdownView;

public class MessageEntryActivity extends Activity {
    private EditText messageInput;
    private MarkdownView markdownView;
    private Button cancelButton;
    private Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_entry);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        messageInput = ((EditText) findViewById(R.id.editText_message));
        markdownView = ((MarkdownView) findViewById(R.id.markdown_message_input));
        cancelButton = ((Button) findViewById(R.id.button_message_cancel));
        sendButton = ((Button) findViewById(R.id.button_message_send));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("text",messageInput.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        messageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                markdownView.loadMarkdown(textView.getText().toString());
                return false;
            }
        });
    }
}
