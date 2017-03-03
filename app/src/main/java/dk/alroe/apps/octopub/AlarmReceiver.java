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

import dk.alroe.apps.octopub.model.Message;
import dk.alroe.apps.octopub.model.Thread;

/**
 * Created by silasa on 2/22/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getSharedPreferences("userData", 0).getBoolean("isOpen", false)) {
            return;
        }
        new updateNotifications(context).execute();
    }

    private class updateNotifications extends AsyncTask<Void, Thread, ArrayList<Thread>> {
        public static final int NEW_THREAD = 1;
        public static final int NEW_MESSAGE = 2;
        public static final int NEW_MESSAGES = 3;
        private Thread thread;
        private Context context;
        Boolean doNotification;
        String contentTitle = "";
        String contentText = "";
        int updateCount = 0;
        int updateType = 0;
        Intent resultIntent;

        public updateNotifications(Context context) {
            this.context = context;
            contentTitle = context.getString(R.string.notif_new_activity);
            doNotification = false;
        }

        protected ArrayList<Thread> doInBackground(Void... voids) {
            ArrayList<Thread> threads = new ArrayList<>();
            try {
                threads = WebRequestHandler.getInstance().getThreads();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SharedPreferences lengthStore = context.getSharedPreferences("threadAlarmLength", 0);
            for (Thread thread: threads
                 ) {
                if (thread.getLength() > lengthStore.getInt(thread.getId(), -2)) {
                    this.thread = thread;
                    if (doNotification) {
                        contentText += "\n";
                    }
                    if (lengthStore.getInt(thread.getId(), -2) == -2) {
                        contentText += context.getString(R.string.notif_new_thread) + thread.getTitle();
                        updateType = NEW_THREAD;
                    } else if (thread.getLength() - lengthStore.getInt(thread.getId(), -2) == 1) {
                        contentText += context.getString(R.string.notif_new_message) + thread.getId();
                        updateType = NEW_MESSAGE;
                    } else {
                        contentText += (thread.getLength() - lengthStore.getInt(thread.getId(), -2) + context.getString(R.string.notif_new_messages) + thread.getId());
                        updateType=NEW_MESSAGES;
                    }
                    updateCount += 1;
                    doNotification = true;
                }
            }

            if (doNotification){
                if (updateCount == 1) {
                    // Creates an explicit intent for an Activity in your app
                    resultIntent = new Intent(context, ThreadActivity.class);
                    resultIntent.putExtra("ThreadID", thread.getId());
                    resultIntent.putExtra("ThreadTitle", thread.getTitle());
                    contentTitle = contentText;
                    if (updateType == NEW_THREAD){
                        try {
                            contentText = WebRequestHandler.getInstance().getThread(thread.getId()).getText();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (updateType == NEW_MESSAGE){
                        contentText = "";
                        try {
                            ArrayList<Message> messages = WebRequestHandler.getInstance().getMessagesFrom(thread.getId(), context.getSharedPreferences("threadAlarmLength", 0).getInt(thread.getId(), -2));
                            for (Message message :
                                    messages) {
                                contentText+=message.getText();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    // Creates an explicit intent for an Activity in your app
                    resultIntent = new Intent(context, MainActivity.class);
                }
            }

            return threads;
        }

        @Override
        protected void onPostExecute(ArrayList<Thread> threads) {
            if (doNotification) {

                int color = Color.parseColor("#" + context.getSharedPreferences("userData", 0).getString("id", context.getString(R.string.appColor)));
                long[] vibratePattern = {28, 250, 40, 100};
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_notifications_active_white_24dp)
                                .setContentTitle(contentTitle)
                                .setContentText(contentText).setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true)
                                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                .setVibrate(vibratePattern)
                                .setColor(color).setLights(color, 500, 2000);//TODO Extract values

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

                    SoundPool sp = new SoundPool(5, AudioManager.STREAM_NOTIFICATION, 0);
                    int soundId = sp.load(context, R.raw.blip, 1);
                    sp.play(soundId, 1, 1, 0, 0, 1);
                    MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.blip);
                    mPlayer.start();
                }
            }
        }
    }
}
