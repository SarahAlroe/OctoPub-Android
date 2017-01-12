package dk.alroe.apps.octopub;

import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class HelpActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HelpAdapter mAdapter;
    private ArrayList<String> helps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mRecyclerView = (RecyclerView) findViewById(R.id.helps_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HelpAdapter(this,helps);
        mRecyclerView.setAdapter(mAdapter);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        toolbar = appToolbar;
        appToolbar.setTitle("Help");
        setSupportActionBar(appToolbar);
        new showHelp().execute(this);
        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }
    }

    private class showHelp extends AsyncTask<AppCompatActivity, Void, String> {
        AppCompatActivity parent;

        protected String doInBackground(AppCompatActivity... appCompatActivities) {
            parent = appCompatActivities[0];
            String help = null;
            try {
                help = WebRequestHandler.getInstance().getHelp();
            }catch (Exception e){
                System.out.println(e);
            }
            return help;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            helps.add(aString);
            mAdapter.notifyDataSetChanged();
        }
    }
}
