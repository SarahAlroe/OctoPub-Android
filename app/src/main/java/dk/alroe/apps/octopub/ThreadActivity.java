package dk.alroe.apps.octopub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import dk.alroe.apps.octopub.model.Message;
import dk.alroe.apps.octopub.model.Thread;

public class ThreadActivity extends BaseActivity {

    private static final int MESSAGE_REQUEST = 1;
    public ArrayList<Message> messages = new ArrayList<>();
    public int currentProgress = -1;
    public String currentThread;
    public Toolbar appToolbar;
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        currentThread = intent.getStringExtra("ThreadID");
        new updateMessages(currentThread, currentProgress).execute();
        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar = appToolbar;
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        setTitle(intent.getStringExtra("ThreadTitle"));
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.message_create_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked();
            }
        });
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (-verticalOffset > appBarLayout.getHeight() / 3) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        setSupportActionBar(appToolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { //TODO would be best to set listener in onResume and remove in onPause. You could get callbacks when activity is not in foreground or Activity==null
            @Override
            public void onRefresh() {
                new updateMessages(currentThread, currentProgress).execute();
            }
        });
        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.doPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.doResume();
    }

    private void fabClicked() {
        Intent intent = new Intent(this, MessageEntryActivity.class);
        startActivityForResult(intent, MESSAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MESSAGE_REQUEST && resultCode == RESULT_OK) {
            String message = data.getStringExtra("text");
            new submitMessage(message).execute(this);
        }
    }

    public void notifyFail(){
        Toast.makeText(getApplicationContext(), "Retrieving thread failed - Network error?", Toast.LENGTH_LONG).show();
        finish();
    }

    public void setTitle(String title) {
        //TextView titleView = (TextView) findViewById(R.id.toolbar_title);
        //titleView.setText(title);
        //((TextView) findViewById(R.id.toolbar_title)).setText(title);
        collapsingToolbar.setTitle(title);
    }

    public void setText(String text) {
        //TODO add subheader thing
        //TextView textView = (TextView) findViewById(R.id.toolbar_text);
        //textView.setText(text);
        //appToolbar.setSubtitle(text);
    }

    private class submitMessage extends AsyncTask<AppCompatActivity, Void, Void> {
        String messageToSend;
        ThreadActivity parent;

        submitMessage(String message) {
            messageToSend = message;
        }

        @Override
        protected Void doInBackground(AppCompatActivity... appCompatActivities) {
            parent = ((ThreadActivity) appCompatActivities[0]);
            try {
                WebRequestHandler.getInstance().addMessage(parent.currentThread, messageToSend, getID());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new updateMessages(currentThread, currentProgress).execute();
        }
    }

    private class updateMessages extends AsyncTask<Void, Message, ArrayList<Message>> {
        String threadToGet;
        int startNumber;

        public updateMessages(String id, int start) {
            super();
            threadToGet = id;
            startNumber = start;
        }

        protected ArrayList<Message> doInBackground(Void... Voids) {
            ArrayList<Message> messages = new ArrayList<>();
            Thread thread = new Thread("", "", 0);
            try {
                thread = WebRequestHandler.getInstance().getThread(threadToGet);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (currentProgress == -1 && messages.size() == 0) {
                Message threadMessage = new Message("#" + thread.getTitle() + "  \n" + thread.getText(), thread.getId(), 0, -1);
                messages.add(threadMessage);
            }
            try {
                messages.addAll(WebRequestHandler.getInstance().getMessagesFrom(threadToGet, startNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return messages;
        }

        @Override
        protected void onPostExecute(ArrayList<Message> newMessages) {
            super.onPostExecute(newMessages);
            if (newMessages==null) {
                notifyFail();
                return;
            }
            if (messages.size() == 0) {//If opening a new thread, add everything at once.
                messages.add(newMessages.get(0));//Add title message and remove from new
                newMessages.remove(0);
                Collections.reverse(newMessages); //Reverse the order for the newest first
                messages.addAll(newMessages);
                mAdapter.notifyDataSetChanged();
            } else {//Else add new messages individually
                for (Message message : newMessages) {
                    messages.add(1, message);
                    mAdapter.notifyItemInserted(1);
                }
            }
            for (Message message : newMessages) {//Then make sure currentProgress is the highest it can be.
                if (currentProgress < message.getNumber()) {
                    currentProgress = message.getNumber();
                }
                //Update threadLength
                SharedPreferences threadLength = getSharedPreferences("threadLength", 0);
                SharedPreferences.Editor lengthEditor = threadLength.edit();
                lengthEditor.putInt(threadToGet, currentProgress);
                lengthEditor.apply();
                //Update threadAlarmLength
                SharedPreferences.Editor editor = getSharedPreferences("threadAlarmLength", 0).edit();
                editor.putInt(threadToGet, currentProgress);
                editor.apply();

            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
