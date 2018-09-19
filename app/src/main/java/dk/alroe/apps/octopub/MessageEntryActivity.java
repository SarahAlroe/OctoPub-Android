package dk.alroe.apps.octopub;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class MessageEntryActivity extends BaseActivity {
    private EditText messageInput;
    private MarkdownViewRework markdownView;
    private ImageButton sendButton;
    private ImageButton contentUploadButton;
    private ImageButton markdownButton;
    private LottieAnimationView uploadAnimation;
    private boolean uploadAnimationEnding;

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
        uploadAnimation = ((LottieAnimationView) findViewById(R.id.animation_upload));

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

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });

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

            uploadAnimationEnding = false;
            uploadAnimation.setVisibility(View.VISIBLE);
            uploadAnimation.setAnimation("upload_animation.json");
            uploadAnimation.loop(false);
            uploadAnimation.playAnimation();
            uploadAnimation.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if ((!uploadAnimationEnding) && uploadAnimation.getProgress() > 0.75f){
                        uploadAnimation.setProgress(0.1f);
                        valueAnimator.setCurrentPlayTime((long)((float)valueAnimator.getDuration()*0.1f));
                    }
                    else if (uploadAnimationEnding && uploadAnimation.getProgress() > 0.95f){
                        uploadAnimation.cancelAnimation();
                        uploadAnimation.setVisibility(View.GONE);
                        if(messageInput.requestFocus()) {
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                }
            });

            switch (requestCode) {
                case PICK_IMAGE: {
                    Uri fileUri = data.getData();
                    new uploadMedia(this).execute(fileUri);
                    break;
                }
                case CAMERA_IMAGE: {
                    Uri fileUri = Uri.parse(currentMediaFilePath);
                    new uploadMedia(this).execute(fileUri);
                    break;
                }
                case PICK_VIDEO: {
                    Uri fileUri = data.getData();
                    new uploadMedia(this).execute(fileUri);
                    break;
                }
                case CAMERA_VIDEO: {
                    Uri fileUri = data.getData();
                    new uploadMedia(this).execute(fileUri);
                    break;
                }
                case PICK_AUDIO: {
                    Uri fileUri = data.getData();
                    new uploadMedia(this).execute(fileUri);
                    break;
                }
                case RECORD_AUDIO: {
                    Uri fileUri = data.getData();
                    File recordedFile = new File(fileUri.getPath());
                    IConvertCallback recordConvertCallback = new IConvertCallback() {
                        @Override
                        public void onSuccess(File file) {
                            callbackUploadMedia(file);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            System.out.println(e);
                            uploadAnimationEnding = true;
                        }
                    };
                    AndroidAudioConverter.with(this)
                            .setFile(recordedFile)
                            .setFormat(AudioFormat.MP3)
                            .setCallback(recordConvertCallback)
                            .convert();
                    break;
                }
            }
        }
        attachmentFragment.dismissAllowingStateLoss();
    }
    private void callbackUploadMedia(File file){
        new uploadMedia(this).execute(Uri.parse(file.getAbsolutePath()));
    }

    private class uploadMedia extends AsyncTask<Uri, Void, String> {

        private final Context context;

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
            uploadAnimationEnding=true;
        }
    }

    private String markdownPrepare(String urlString) {
        return " ![](https://octopub.cf/img/" + urlString + ") "; //TODO Extract to var
    }
}
