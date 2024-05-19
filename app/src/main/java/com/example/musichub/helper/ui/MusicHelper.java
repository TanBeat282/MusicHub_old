package com.example.musichub.helper.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.PlayNowActivity;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.service.MyService;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class MusicHelper {

    private Context context;
    private Items mSong;
    private boolean isPlaying;
    private int action;
    private SharedPreferencesManager sharedPreferencesManager;
    private View layoutPlayerBottom;

    private LinearLayout linearPlayPause, linearNext;
    private ImageView imgAlbumSong, imgPlayPause;
    private TextView tvTitleSong, tvSingleSong;
    private LinearProgressIndicator progressIndicator;
    private PlayingStatusUpdater playingStatusUpdater;

    public MusicHelper(Context context, SharedPreferencesManager sharedPreferencesManager) {
        this.context = context;
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public void initAdapter(PlayingStatusUpdater playingStatusUpdater) {
        this.playingStatusUpdater = playingStatusUpdater;
    }

    public void initViews(View layoutPlayerBottom, LinearLayout layoutPlayer, LinearLayout linearPlayPause,
                          ImageView imgPlayPause, LinearLayout linearNext, ImageView imgAlbumSong,
                          TextView tvTitleSong, TextView tvSingleSong, LinearProgressIndicator progressIndicator) {
        this.layoutPlayerBottom = layoutPlayerBottom;
        this.linearPlayPause = linearPlayPause;
        this.imgPlayPause = imgPlayPause;
        this.linearNext = linearNext;
        this.imgAlbumSong = imgAlbumSong;
        this.tvTitleSong = tvTitleSong;
        this.tvSingleSong = tvSingleSong;
        this.progressIndicator = progressIndicator;

        layoutPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayNowActivity.class);
            context.startActivity(intent);
        });
    }

    public BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    return;
                }
                mSong = (Items) bundle.get("object_song");
                isPlaying = bundle.getBoolean("status_player");
                action = bundle.getInt("action_music");
                handleLayoutMusic(action);
                checkIsPlayingPlaylist(mSong, sharedPreferencesManager.restoreSongArrayList(), playingStatusUpdater);
            }
        };
    }

    public BroadcastReceiver createSeekBarUpdateReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currentTime = intent.getIntExtra("current_time", 0);
                int totalTime = intent.getIntExtra("total_time", 0);
                updateIndicator(currentTime, totalTime);
            }
        };
    }

    public void registerReceivers() {
        LocalBroadcastManager.getInstance(context).registerReceiver(createBroadcastReceiver(), new IntentFilter("send_data_to_activity"));
        LocalBroadcastManager.getInstance(context).registerReceiver(createSeekBarUpdateReceiver(), new IntentFilter("send_seekbar_update"));
    }

    public void unregisterReceivers() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(createBroadcastReceiver());
        LocalBroadcastManager.getInstance(context).unregisterReceiver(createSeekBarUpdateReceiver());
    }

    public void sendActionToService(int action) {
        Intent intent = new Intent(context, MyService.class);
        intent.putExtra("action_music_service", action);
        context.startService(intent);
    }

    public void showInfoSong() {
        if (mSong == null) {
            return;
        }

        if (context instanceof Activity && !((Activity) context).isDestroyed()) {
            Glide.with(context)
                    .load(mSong.getThumbnail())
                    .into(imgAlbumSong);
        }

        tvTitleSong.setText(mSong.getTitle());
        tvSingleSong.setText(mSong.getArtistsNames());

        linearPlayPause.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(context, MyService.class)) {
                context.startService(new Intent(context, MyService.class));
            }
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
            }
        });

        linearNext.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(context, MyService.class)) {
                context.startService(new Intent(context, MyService.class));
            }
            sendActionToService(MyService.ACTION_NEXT);
        });

        int color = ContextCompat.getColor(context, R.color.gray);
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        ViewCompat.setBackgroundTintList(layoutPlayerBottom, colorStateList);
    }

    public void setStatusButtonPlayOrPause() {
        if (!Helper.isMyServiceRunning(context, MyService.class)) {
            isPlaying = false;
        }
        imgPlayPause.setImageResource(isPlaying ? R.drawable.baseline_pause_24 : R.drawable.baseline_play_arrow_24);
    }

    public void handleLayoutMusic(int action) {
        switch (action) {
            case MyService.ACTION_START:
            case MyService.ACTION_PAUSE:
            case MyService.ACTION_RESUME:
                layoutPlayerBottom.setVisibility(View.VISIBLE);
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                layoutPlayerBottom.setVisibility(View.GONE);
                break;
        }
    }

    public void getSongCurrent() {
        mSong = sharedPreferencesManager.restoreSongState();
        isPlaying = sharedPreferencesManager.restoreIsPlayState();
        action = sharedPreferencesManager.restoreActionState();
        handleLayoutMusic(action);
    }

    public void updateIndicator(int currentTime, int totalTime) {
        if (totalTime > 0) {
            float progress = (float) currentTime / totalTime;
            int progressInt = (int) (progress * 100);
            progressIndicator.setProgressCompat(progressInt, true);
        }
    }

    public void checkIsPlayingPlaylist(Items items, ArrayList<Items> songList, PlayingStatusUpdater adapter) {
        if (items == null || songList == null || adapter == null) {
            return;
        }

        String currentEncodeId = items.getEncodeId();
        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
            adapter.updatePlayingStatus(currentEncodeId);
        }
    }


}
