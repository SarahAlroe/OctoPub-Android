package dk.alroe.apps.octopub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private ThreadAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Thread> threads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.thread_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ThreadAdapter(MainActivity.this, threads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Thread item) {
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                goToThread(item);
            }
        });
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        toolbar = appToolbar;
        setSupportActionBar(appToolbar);
        new updateThreads().execute(this);
        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }
    }

    private class updateThreads extends AsyncTask<AppCompatActivity, Thread, Void> {

        protected Void doInBackground(AppCompatActivity... appCompatActivities) {
            ArrayList<Thread> threads = new ArrayList<>();
            try {
                threads = WebRequestHandler.getInstance().getThreads();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Thread thread : threads) {
                publishProgress(thread);
            }
            return null;
        }

        protected void onProgressUpdate(Thread... thread) {
            threads.add(thread[0]);
            mAdapter.notifyDataSetChanged();
        }
    }
}