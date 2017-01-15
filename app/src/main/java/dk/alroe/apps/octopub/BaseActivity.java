package dk.alroe.apps.octopub;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;

/**
 * Created by silasa on 1/4/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public CollapsingToolbarLayout collapsingToolbar;
    public Toolbar toolbar;
    private Menu menu;

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();

        //TODO would be easier to read if moved to one method eg. SetUIColor(BRIGHT)
        if (ColorHelper.isBrightColor(Color.parseColor("#" + getID().getId()))) {
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarLight);
            inflater.inflate(R.menu.default_menu_black, menu);
            collapsingToolbar.setExpandedTitleColor(Color.BLACK);
            collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.AppBarDark);
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_black_24dp));
            //setTheme(R.style.AppTheme);
        } else {
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarDark);
            inflater.inflate(R.menu.default_menu_white, menu);
            collapsingToolbar.setExpandedTitleColor(Color.WHITE);
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
            //setTheme(R.style.AppThemeDark);
        }
        menu.findItem(R.id.view_id).setTitle(getID().getId());
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        if (ColorHelper.isBrightColor(Color.parseColor("#" + getID().getId()))) { //TODO a comment might be necessary here. Why setting color to getId?
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarLight);
        } else {
            getSupportActionBar().getThemedContext().setTheme(R.style.AppBarDark);
        }
        super.onCreate(savedInstanceState, persistentState);
    }

    void goToHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    void goToThreadAdd() {
        Intent intent = new Intent(this, ThreadAddActivity.class);
        startActivity(intent);
    }

    void goToThread(Thread thread) {
        Intent intent = new Intent(this, ThreadActivity.class);
        intent.putExtra("ThreadID", thread.getId());
        intent.putExtra("ThreadTitle", thread.getTitle());
        startActivity(intent);
    }

    protected ID getID() {
        SharedPreferences sp = getSharedPreferences("userData", 0);
        String id = sp.getString("id", "009688"); //TODO extract magic number to constant with a good name
        String hash = sp.getString("hash", null);
        return new ID(id, hash);
    }

    protected void updateActionBar() {
        int bgColor = Color.parseColor("#" + getID().getId());
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(bgColor));
        //collapsingToolbar.setContentScrimColor(bgColor);
        collapsingToolbar.setBackgroundColor(bgColor);
        if (menu != null) {
            menu.findItem(R.id.view_id).setTitle(getID().getId());
        }
        invalidateOptionsMenu();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String title = ((String) collapsingToolbar.getTitle());
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            setTaskDescription(new ActivityManager.TaskDescription(title, icon, bgColor));
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_addThread:
                goToThreadAdd();
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
}
