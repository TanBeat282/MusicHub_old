package com.example.musichub.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.musichub.service.MyService;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action", 0);
        Intent intentService = new Intent(context, MyService.class);
        intentService.putExtra("action_music_service", actionMusic);

        context.startService(intentService);
    }
}
