package dk.alroe.apps.octopub;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
    public String title;
    public String text;
    public Toolbar appToolbar;

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
        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar = appToolbar;
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.message_create_button);
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (-verticalOffset>appBarLayout.getHeight()/3) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        setSupportActionBar(appToolbar);
        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    public void setTitle(String title){
        //TextView titleView = (TextView) findViewById(R.id.toolbar_title);
        //titleView.setText(title);
        appToolbar.setTitle(title);
    }
    public void setText(String text){
        //TextView textView = (TextView) findViewById(R.id.toolbar_text);
        //textView.setText(text);
        //appToolbar.setSubtitle(text);
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
            ThreadActivity realParent = (ThreadActivity) parent;
            Thread thread = new Thread("","",0);
            try {
                thread = WebRequestHandler.getInstance().getThread(threadToGet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message threadMessage = new Message("#"+thread.getTitle()+"  \n"+thread.getText(),thread.getId(),1337,-1);
            realParent.setTitle(thread.getTitle());
            realParent.setText(thread.getText());
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
