package dk.alroe.apps.octopub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new updateThreads().execute(this);

    }
    private class updateThreads extends AsyncTask<AppCompatActivity, Thread, Void> {
        AppCompatActivity parent;
        protected Void doInBackground(AppCompatActivity... appCompatActivities) {
            parent = appCompatActivities[0];
            ArrayList<Thread> threads = new ArrayList<>();
            try {
                threads = WebRequestHandler.getInstance().getThreads();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Thread thread : threads){
                publishProgress(thread);
            }
            return null;
        }

        protected void onProgressUpdate(Thread ... thread) {
            TextView textView = new TextView(parent);
            textView.setTextSize(40);
            textView.setText(thread[0].getTitle());

            ViewGroup layout = (ViewGroup) findViewById(R.id.thread_view);
            layout.addView(textView);
        }
    }
    /*public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        try {
            WebRequestHandler.getInstance().getThreads();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }*/
}