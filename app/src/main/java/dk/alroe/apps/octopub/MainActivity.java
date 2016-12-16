package dk.alroe.apps.octopub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ThreadAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public  ArrayList<Thread> threads = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (noID()){
            requestID();
        }
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
        new updateThreads().execute(this);

    }

    private void goToThread(Thread thread) {
        Intent intent = new Intent(this, ThreadActivity.class);
        intent.putExtra("ThreadID", thread.getId());
        startActivity(intent);
    }

    private void requestID(){
            ID idClass;
            try {
                idClass = WebRequestHandler.getInstance().newID();
                updateID(idClass);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void updateID(ID idClass) {
        SharedPreferences userData = getSharedPreferences("userData", 0);
        String id = idClass.getId();
        String hash = idClass.getHash();
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("id",id);
        editor.putString("hash",hash);
        editor.apply();
    }

    private boolean noID(){
        SharedPreferences userData = getSharedPreferences("userData", 0);
        String id = userData.getString("id",null);
        return (id == null);
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
            threads.add(thread[0]);
            mAdapter.notifyDataSetChanged();
        }
    }
}