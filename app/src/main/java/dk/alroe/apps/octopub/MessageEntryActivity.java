package dk.alroe.apps.octopub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.IOException;

public class MessageEntryActivity extends BaseActivity {
    private EditText messageInput;
    private MarkdownViewRework markdownView;
    private ImageButton sendButton;
    private ImageButton contentUploadButton;
    private ImageButton markdownButton;

    private BottomSheetDialogFragment attachmentFragment;

    public String currentMediaFilePath;

    public static final int PICK_IMAGE = 1;
    public static final int CAMERA_IMAGE = 2;
    public static final int PICK_VIDEO = 3;
    public static final int CAMERA_VIDEO = 4;
    public static final int PICK_AUDIO = 5;
    public static final int RECORD_AUDIO = 6;
    private boolean markdownViewIsCollapsed=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_entry);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        messageInput = ((EditText) findViewById(R.id.editText_message));
        markdownView = ((MarkdownViewRework) findViewById(R.id.markdown_message_input));
        sendButton = ((ImageButton) findViewById(R.id.button_message_send));
        contentUploadButton = ((ImageButton) findViewById(R.id.button_attach));
        markdownButton = ((ImageButton) findViewById(R.id.button_markdown));

        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        //collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        //collapsingToolbar.setScrimsShown(false);
        toolbar = appToolbar;
        setSupportActionBar(appToolbar);

        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }

        contentUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachmentFragment = new AttachmentFragment();
                attachmentFragment.show(getSupportFragmentManager(), attachmentFragment.getTag());
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

        markdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (markdownViewIsCollapsed){
                    markdownView.loadMarkdown(messageInput.getText().toString(), "file:///android_res/raw/style.css");
                    markdownView.getSettings().setJavaScriptEnabled(true);
                    LinearLayout.LayoutParams mDParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
                    markdownView.setLayoutParams(mDParams);
                    LinearLayout.LayoutParams mIParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.0f);
                    messageInput.setLayoutParams(mIParams);
                    markdownButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off_black_24dp));
                    markdownViewIsCollapsed = false;
                }else {
                    LinearLayout.LayoutParams mDParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.0f);
                    markdownView.setLayoutParams(mDParams);
                    LinearLayout.LayoutParams mIParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
                    messageInput.setLayoutParams(mIParams);
                    markdownButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_black_24dp));
                    markdownViewIsCollapsed = true;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri fileUri = data.getData();
                new uploadMedia(this).execute(fileUri);

            } else if (requestCode == CAMERA_IMAGE) {
                Uri fileUri = Uri.parse(currentMediaFilePath);
                new uploadMedia(this).execute(fileUri);
            } else if (requestCode == PICK_VIDEO) {
                Uri fileUri = data.getData();
                new uploadMedia(this).execute(fileUri);

            } else if (requestCode == CAMERA_VIDEO) {
                Uri fileUri = data.getData();
                new uploadMedia(this).execute(fileUri);

            } else if (requestCode == PICK_AUDIO) {
                Uri fileUri = data.getData();
                new uploadMedia(this).execute(fileUri);

            } else if (requestCode == RECORD_AUDIO) {
                Uri fileUri = data.getData();
                new uploadMedia(this).execute(fileUri);
            }
        }
        attachmentFragment.dismissAllowingStateLoss();
    }

    private class uploadMedia extends AsyncTask<Uri, Void, String> {

        private Context context;

        uploadMedia(Context context) {
            this.context = context;
        }

        protected String doInBackground(Uri... uris) {
            try {
                return WebRequestHandler.getInstance().uploadFromUri(uris[0], context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String urlString) {
            super.onPostExecute(urlString);
            String markdownUrl = markdownPrepare(urlString);
            int start = Math.max(messageInput.getSelectionStart(), 0);
            int end = Math.max(messageInput.getSelectionEnd(), 0);
            messageInput.getText().replace(Math.min(start, end), Math.max(start, end),
                    markdownUrl, 0, markdownUrl.length());
        }
    }

    private String markdownPrepare(String urlString) {
        return " ![](https://octopub.tk/img/" + urlString + ") "; //TODO Extract to var
    }
}
