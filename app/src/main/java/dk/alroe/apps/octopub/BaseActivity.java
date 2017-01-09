package dk.alroe.apps.octopub;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;

/**
 * Created by silasa on 1/4/17.
 */

public class BaseActivity extends AppCompatActivity {
    private Menu menu;
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        if (ColorHelper.isBrightColor(Color.parseColor("#"+getID().getId()))){
            inflater.inflate(R.menu.default_menu_black, menu);
            //setTheme(R.style.AppTheme);
        }else{
            inflater.inflate(R.menu.default_menu_white, menu);
            //setTheme(R.style.AppThemeDark);
        }
        menu.findItem(R.id.view_id).setTitle(getID().getId());
        //getSupportActionBar().getThemedContext().getTheme().applyStyle(R.style.ToolbarBright,true);
        return true;
    }

    protected ID getID() {
        SharedPreferences sp = getSharedPreferences("userData", 0);
        String id = sp.getString("id", null);
        String hash = sp.getString("hash", null);
        return new ID(id, hash);
    }
    protected void updateActionBar() {
        int bgColor = Color.parseColor("#" + getID().getId());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(bgColor));
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
            case R.id.action_addThread:
                //TODO add new thread
                return true;

            case R.id.action_help:
                //TODO add help screen
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
