package dk.alroe.apps.octopub;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

/**
 * Created by silasa on 2/22/17.
 */

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences userData = context.getSharedPreferences("userData", 0);
            if (userData.getBoolean("messageAlarm", false)) {
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
            }
        }
    }
}
