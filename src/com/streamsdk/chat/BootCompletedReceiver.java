package com.streamsdk.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.streamsdk.xmpp.XMPPConnectionService;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("in boot completed reciever", "starting service...");
        context.startService(new Intent(context, XMPPConnectionService.class));
    }
}