package dk.alroe.apps.octopub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class HelpActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HelpAdapter mAdapter;
    private final ArrayList<String> helps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mRecyclerView = (RecyclerView) findViewById(R.id.helps_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HelpAdapter(this, helps);
        mRecyclerView.setAdapter(mAdapter);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        toolbar = appToolbar;
        setSupportActionBar(appToolbar);
        new showHelp().execute(this);
        if (noID()) {
            new requestID().execute(); //TODO. It would be better to load new data in OnResume to assure that user gets the newest data when app is resumed
            // TODO Furthermore AsyncTasks and other background tasks and listeners should be canceled in OnPause.
        } else {
            updateActionBar();
        }
    }

    private class showHelp extends AsyncTask<AppCompatActivity, Void, String> {

        protected String doInBackground(AppCompatActivity... appCompatActivities) {
            String help = null;
            try {
                help = WebRequestHandler.getInstance().getHelp();
            } catch (Exception ignored) {
                help = getString(R.string.error_get_help_failed);
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
