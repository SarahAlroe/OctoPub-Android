package dk.alroe.apps.octopub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public  ArrayList<Message> messages = new ArrayList<>();
    public int currentProgress = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        mRecyclerView = (RecyclerView) findViewById(R.id.message_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(ThreadActivity.this, messages);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemViewCacheSize(2);
        final Intent intent = getIntent();
        new updateMessages(intent.getStringExtra("ThreadID"), currentProgress).execute(this);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }
    }

    private class updateMessages extends AsyncTask<AppCompatActivity, Message, Void> {
        AppCompatActivity parent;
        String threadToGet;
        int startNumber;
        public updateMessages(String id, int start){
            super();
            threadToGet = id;
            startNumber = start;
        }
        protected Void doInBackground(AppCompatActivity... appCompatActivities) {
            parent = appCompatActivities[0];
            Thread thread = new Thread("","",0);
            try {
                thread = WebRequestHandler.getInstance().getThread(threadToGet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message threadMessage = new Message("#"+thread.getTitle()+"  \n"+thread.getText(),thread.getId(),1337,-1);
            publishProgress(threadMessage);
            ArrayList<Message> messages = new ArrayList<>();
            try {
                messages = WebRequestHandler.getInstance().getMessagesFrom(threadToGet, startNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Message message : messages){
                publishProgress(message);
            }
            return null;
        }

        protected void onProgressUpdate(Message ... messageList) {
            Message message = messageList[0];
            if (messages.size()!=0) {
                messages.add(1, message);
            }else{
                messages.add(message);
            }
            currentProgress = message.getNumber()-1;
            mAdapter.notifyDataSetChanged();
        }
    }
}
