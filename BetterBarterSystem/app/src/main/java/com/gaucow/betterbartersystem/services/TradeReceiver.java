package com.gaucow.betterbartersystem.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class TradeReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.gaucow.betterbartersystem.action.TRADE_ACTION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), ACTION)) {
            Intent i = new Intent(context, TradeService.class);
            i.putExtra("complexity", intent.getStringExtra("complexity"));
            context.startService(i);
        }
    }
}
