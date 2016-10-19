package com.zdr.geekmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zdr.geekmusic.service.MusicService;

/**
 * Created by zdr on 16-9-9.
 */
public class MusicReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Intent i = new Intent(context, MusicService.class);
            i.setAction(MusicService.NOTIFICATION_PAUSE);
            context.startService(i);
        }
    }
}
