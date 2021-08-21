package com.gaucow.betterbartersystem.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import java.util.Objects;

public class TradeBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent receiveIntent) {
        if(Objects.equals(receiveIntent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            SharedPreferences prefs = context.getSharedPreferences("notif", Context.MODE_PRIVATE);
            String timeString = prefs.getString("time", "");
            long frequency = 0;
            if(!timeString.equals("")) {
                if (timeString.equals("hour")) {
                    frequency = AlarmManager.INTERVAL_HOUR;
                } else if (timeString.equals("day")) {
                    frequency = AlarmManager.INTERVAL_DAY;
                }
                Intent intent = new Intent(context, TradeReceiver.class);
                intent.setAction(TradeReceiver.ACTION);
                String complexOrSimple = prefs.getString("complexity", "");
                intent.putExtra("complexity", complexOrSimple);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Objects.requireNonNull(alarm).setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() +
                                frequency, frequency, pendingIntent);
            }
        }
    }
}
