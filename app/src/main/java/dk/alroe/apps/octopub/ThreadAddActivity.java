package dk.alroe.apps.octopub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ThreadAddActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_add);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.app_toolbar_collapsing);
        toolbar = appToolbar;
        setSupportActionBar(appToolbar);
        if (noID()) {
            new requestID().execute();
        } else {
            updateActionBar();
        }
        Button submitButton = ((Button) findViewById(R.id.button_submit));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addThreadClicked();
            }
        });
    }

    private void addThreadClicked() {
        EditText titleInput = ((EditText) findViewById(R.id.editText_title));
        EditText textInput = ((EditText) findViewById(R.id.editText_text));
        Thread newThread = new Thread(titleInput.getText().toString(), "NEWTHD", 0, textInput.getText().toString());
        new submitThread(newThread).execute(this);
    }

    private class submitThread extends AsyncTask<AppCompatActivity, Void, ID> {
        private Thread threadToSubmit;

        submitThread(Thread thread) {
            super();
            threadToSubmit = thread;
        }

        protected ID doInBackground(AppCompatActivity... appCompatActivities) {
            ID id = null;
            try {
                id = WebRequestHandler.getInstance().addThread(threadToSubmit);
            } catch (Exception e) {
                System.out.println(e);
            }
            return id;
        }

        @Override
        protected void onPostExecute(ID newID) {
            super.onPostExecute(newID);
            updateID(newID);
            Thread transitionThread = new Thread(threadToSubmit.getTitle(), newID.getId(), 0, threadToSubmit.getText());
            goToThread(transitionThread);
        }
    }

}
