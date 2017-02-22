package dk.alroe.apps.octopub;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.IOException;
import java.util.ArrayList;

import dk.alroe.apps.octopub.model.Thread;

/**
 * Created by silasa on 2/22/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getSharedPreferences("userData",0).getBoolean("isOpen",false)){return;}
        new updateThreads(context).execute();
    }

    private class updateThreads extends AsyncTask<Void, Thread, Void> {
        private Context context;
        Boolean doNotification;
        String contentText = "";

        public updateThreads(Context context) {
            this.context = context;
            doNotification = false;
        }

        protected Void doInBackground(Void... voids) {
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

        protected void onProgressUpdate(Thread... threads) {
            Thread thread = threads[0];
            SharedPreferences lengthStore = context.getSharedPreferences("threadAlarmLength", 0);
            if (thread.getLength() > lengthStore.getInt(thread.getId(), -2)) {
                if (doNotification) {
                    contentText += "\n";
                }
                if (lengthStore.getInt(thread.getId(), -2) == -2) {
                    contentText += "New thread: " + thread.getTitle();
                } else if (thread.getLength() - lengthStore.getInt(thread.getId(), -2) == 1) {
                    contentText += "1 new message in " + thread.getId();
                } else {
                    contentText += (thread.getLength() - lengthStore.getInt(thread.getId(), -2) + " new messages in " + thread.getId());
                }
                doNotification = true;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (doNotification) {
                int color = Color.parseColor("#" + context.getSharedPreferences("userData", 0).getString("id", context.getString(R.string.appColor)));
                long[] vibratePattern = {28, 250, 40, 100};
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_notifications_active_white_24dp)
                                .setContentTitle("New activity on OctoPub!")
                                .setContentText(contentText).setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true)
                                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                .setVibrate(vibratePattern)
                                .setColor(color).setLights(color, 500, 2000);//TODO Extract values
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(context, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
                //Only play sound once since application was last opened
                if (context.getSharedPreferences("userData", 0).getBoolean("wasLastOpen", true)) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("userData", 0).edit();
                    editor.putBoolean("wasLastOpen", false);
                    editor.apply();

                    SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                    int soundId = sp.load(context, R.raw.blip, 1);
                    sp.play(soundId, 1, 1, 0, 0, 1);
                    MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.blip);
                    mPlayer.start();
                }
            }
        }
    }
}
