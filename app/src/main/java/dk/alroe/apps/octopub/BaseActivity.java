package dk.alroe.apps.octopub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;

/**
 * Created by silasa on 1/4/17.
 */

public class BaseActivity extends AppCompatActivity {
    private Menu menu;
    public CollapsingToolbarLayout collapsingToolbar;
    public Toolbar toolbar;
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        //mToolbar.setNavigationIcon(R.mipmap.ic_launcher);
        //mToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_menu));
        if (ColorHelper.isBrightColor(Color.parseColor("#"+getID().getId()))){
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarLight);
            inflater.inflate(R.menu.default_menu_black, menu);
            collapsingToolbar.setExpandedTitleColor(Color.BLACK);
            collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.AppBarDark);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_black_24dp));
            //setTheme(R.style.AppTheme);
        }else{
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarDark);
            inflater.inflate(R.menu.default_menu_white, menu);
            collapsingToolbar.setExpandedTitleColor(Color.WHITE);
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
            //setTheme(R.style.AppThemeDark);
        }
        menu.findItem(R.id.view_id).setTitle(getID().getId());
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        if (ColorHelper.isBrightColor(Color.parseColor("#"+getID().getId()))) {
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarLight);
        }else {
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarDark);
        }
        super.onCreate(savedInstanceState, persistentState);
    }

    private void goToHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    protected ID getID() {
        SharedPreferences sp = getSharedPreferences("userData", 0);
        String id = sp.getString("id", null);
        String hash = sp.getString("hash", null);
        return new ID(id, hash);
    }
    protected void updateActionBar() {
        int bgColor = Color.parseColor("#" + getID().getId());
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(bgColor));
        //collapsingToolbar.setContentScrimColor(bgColor);
        collapsingToolbar.setBackgroundColor(bgColor);
        if (menu!=null) {
            menu.findItem(R.id.view_id).setTitle(getID().getId());
        }
        invalidateOptionsMenu();
    }
    protected void updateID(ID idClass) {
        SharedPreferences userData = getSharedPreferences("userData", 0);
        String id = idClass.getId();
        String hash = idClass.getHash();
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("id", id);
        editor.putString("hash", hash);
        editor.apply();
        updateActionBar();
        invalidateOptionsMenu();
    }

    protected boolean noID() {
        SharedPreferences userData = getSharedPreferences("userData", 0);
        String id = userData.getString("id", null);
        return (id == null);
    }
    protected class requestID extends AsyncTask<Void, Void, ID> {
        protected ID doInBackground(Void... voids) {
            try {
                return WebRequestHandler.getInstance().newID();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ID id) {
            super.onPostExecute(id);
            updateID(id);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_addThread:
                //TODO add new thread
                return true;

            case R.id.action_help:
                goToHelp();
                return true;
            case R.id.action_refresh_id:
                new requestID().execute();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
