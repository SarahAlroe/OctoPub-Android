package dk.alroe.apps.octopub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
                intent.putExtra("text", messageInput.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                markdownView.loadMarkdown(messageInput.getText().toString());
            }
        });
    }
}
