package com.example.musichub.service;

import static com.example.musichub.application.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.musichub.MainActivity;

import com.example.musichub.helper.uliti.GetUrlAudioHelper;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.song.SongAudio;
import com.example.musichub.receiver.MyReceiver;
import com.example.musichub.R;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;
    public static final int ACTION_NEXT = 5;
    public static final int ACTION_PREVIOUS = 6;
    private boolean isPlaying = false;
    private Items mSong;
    private String currentSongId = "";
    private ArrayList<Items> mSongList;
    private int mPositionSong = -1;
    private SharedPreferencesManager sharedPreferencesManager;
    private final Handler seekBarHandler = new Handler();
    private final Handler stopServiceHandler = new Handler();
    private final Handler autoNextSongHandler = new Handler();
    private final GetUrlAudioHelper getUrlAudioHelper = new GetUrlAudioHelper();


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Items song = (Items) bundle.getSerializable("object_song");
            int positionSong = bundle.getInt("position_song");
            if (bundle.containsKey("song_list")) {
                mSongList = (ArrayList<Items>) bundle.getSerializable("song_list");
            }

            if (song != null) {
                mSong = song;
                mPositionSong = positionSong;
                startMusic(song);
                sendNotificationMedia(song, true);
                saveSongListAndPosition(mSong, positionSong, mSongList);
            }
        } else {
            getSongListAndPosition();
        }
        if (intent.hasExtra("seek_to_position")) {
            int seekToPosition = intent.getIntExtra("seek_to_position", 0);

            // Đảm bảo rằng MediaPlayer đã được khởi tạo
            if (mediaPlayer != null) {
                // Seek đến vị trí mới
                mediaPlayer.seekTo(seekToPosition);
            }
            if (!isPlaying) {
                handleActionMusic(ACTION_RESUME);
            }
        }


        int actionMusic = intent.getIntExtra("action_music_service", 0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void saveSongListAndPosition(Items mSong, int positionSong, ArrayList<Items> mSongList) {
        sharedPreferencesManager.saveSongState(mSong);
        sharedPreferencesManager.saveSongPosition(positionSong);
        sharedPreferencesManager.saveSongArrayList(mSongList);
    }

    private void getSongListAndPosition() {
        ArrayList<Items> songs = sharedPreferencesManager.restoreSongArrayList();
        int position = sharedPreferencesManager.restoreSongPosition();
        Items song = sharedPreferencesManager.restoreSongState();

        mSongList = songs;
        mSong = song;
        mPositionSong = position;
        startMusic(song);
        sendNotificationMedia(song, true);
        saveSongListAndPosition(mSong, position, mSongList);
    }

    private void startMusic(Items song) {
        getColor(song.getThumbnailM());
        getUrlAudioHelper.getSongAudio(song.getEncodeId(), new GetUrlAudioHelper.SongAudioCallback() {
            @Override
            public void onSuccess(SongAudio songAudio) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();

                    // Set audio attributes for MediaPlayer
                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
                }

                if (sharedPreferencesManager.restoreIsRepeatOneState()) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(songAudio.getData().getLow());
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(mp -> {

                            mediaPlayer.start();
                            isPlaying = true;
                            currentSongId = song.getEncodeId();
                            sendActionToActivity(ACTION_START);
                            startUpdatingSeekBar();

                            sharedPreferencesManager.saveSongState(song);
                            sharedPreferencesManager.saveIsPlayState(true);
                            sharedPreferencesManager.saveActionState(MyService.ACTION_START);
                            sharedPreferencesManager.saveSongArrayListHistory(song);

                            autoNextSongHandler.removeCallbacks(autoNextSongRunnable);
                            autoNextSongHandler.postDelayed(autoNextSongRunnable, 1000);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!currentSongId.equals(song.getEncodeId())) {
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(songAudio.getData().getLow());
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(mp -> {

                                mediaPlayer.start();
                                isPlaying = true;
                                currentSongId = song.getEncodeId();
                                sendActionToActivity(ACTION_START);
                                startUpdatingSeekBar();

                                sharedPreferencesManager.saveSongState(song);
                                sharedPreferencesManager.saveIsPlayState(true);
                                sharedPreferencesManager.saveActionState(MyService.ACTION_START);
                                sharedPreferencesManager.saveSongArrayListHistory(song);

                                autoNextSongHandler.removeCallbacks(autoNextSongRunnable);
                                autoNextSongHandler.postDelayed(autoNextSongRunnable, 1000);
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                stopUpdatingSeekBar();
                break;
            case ACTION_RESUME:
                resumeMusic();
                startUpdatingSeekBar();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                stopUpdatingSeekBar();
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
            case ACTION_PREVIOUS:
                previousMusic();
                break;
        }
    }

    private void nextMusic() {
        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset(); // Reset mediaPlayer
                isPlaying = false;
            }

            // Nếu không phải trạng thái lặp lại một bài hát
            if (!sharedPreferencesManager.restoreIsRepeatOneState()) {
                if (sharedPreferencesManager.restoreIsShuffleState()) {
                    // Trạng thái shuffle
                    Random random = new Random();
                    mPositionSong = random.nextInt(mSongList.size()); // Chọn một chỉ số ngẫu nhiên
                } else {
                    // Trạng thái không shuffle
                    mPositionSong++;
                    if (mPositionSong >= mSongList.size()) {
                        mPositionSong = 0;
                    }
                }
            } // Không cần thay đổi mPositionSong nếu đang trong trạng thái lặp lại một bài

            if (mPositionSong >= 0 && mPositionSong < mSongList.size()) {
                // Lấy bài hát từ danh sách và phát
                mSong = mSongList.get(mPositionSong);
                startMusic(mSong);
                sendNotificationMedia(mSong, true);
                saveSongListAndPosition(mSong, mPositionSong, mSongList);
            }
        }
    }


    private void previousMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset(); // Reset mediaPlayer
                isPlaying = false;
            }

            if (!sharedPreferencesManager.restoreIsRepeatOneState()) {

                if (sharedPreferencesManager.restoreIsShuffleState()) {
                    Random random = new Random();
                    mPositionSong = random.nextInt(mSongList.size()); // Chọn một chỉ số ngẫu nhiên
                } else {
                    if (mPositionSong < 0) {
                        mPositionSong = mSongList.size() - 1;
                    } else {
                        mPositionSong--;
                    }
                }
            }

            if (mPositionSong >= 0 && mPositionSong < mSongList.size()) {
                mSong = mSongList.get(mPositionSong);
                startMusic(mSong);
                sendNotificationMedia(mSong, true);
                saveSongListAndPosition(mSong, mPositionSong, mSongList);
            }
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotificationMedia(mSong, false);
            sendActionToActivity(ACTION_PAUSE);

            sharedPreferencesManager.saveIsPlayState(false);
            sharedPreferencesManager.saveActionState(MyService.ACTION_PAUSE);

            stopServiceHandler.removeCallbacks(stopServiceRunnable);
            stopServiceHandler.postDelayed(stopServiceRunnable, 15 * 60 * 1000);

            autoNextSongHandler.removeCallbacks(autoNextSongRunnable);
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            sendNotificationMedia(mSong, true);
            sendActionToActivity(ACTION_RESUME);

            sharedPreferencesManager.saveIsPlayState(true);
            sharedPreferencesManager.saveActionState(MyService.ACTION_RESUME);

            stopServiceHandler.removeCallbacks(stopServiceRunnable);

            autoNextSongHandler.removeCallbacks(autoNextSongRunnable);
            autoNextSongHandler.postDelayed(autoNextSongRunnable, 1000);
        }
    }

    private void sendNotificationMedia(Items song, boolean isPlaying) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(song.getThumbnail())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "tag");

                        mediaSessionCompat.setMetadata(
                                new MediaMetadataCompat.Builder()
                                        .putString(MediaMetadata.METADATA_KEY_TITLE, mSong.getTitle())
                                        .putString(MediaMetadata.METADATA_KEY_ARTIST, mSong.getArtistsNames())
                                        .build());

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_android_black_24dp)
                                .setContentTitle(song.getTitle())
                                .setContentText(song.getArtistsNames())
                                .setLargeIcon(resource)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                        .setShowActionsInCompactView(0, 1, 2)
                                        .setMediaSession(mediaSessionCompat.getSessionToken()));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                        if (isPlaying) {
                            // Cập nhật trạng thái phát nhạc và hiển thị các nút tương ứng
                            notificationBuilder
                                    .addAction(R.drawable.baseline_skip_previous_24, "Previous", getPendingIntent(getApplicationContext(), ACTION_PREVIOUS)) // #0
                                    .addAction(R.drawable.baseline_pause_24, "Pause", getPendingIntent(getApplicationContext(), ACTION_PAUSE)) // #1
                                    .addAction(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(getApplicationContext(), ACTION_NEXT)); // #2
                            notificationBuilder.setContentIntent(pendingIntent);
                        } else {
                            // Cập nhật trạng thái dừng và hiển thị nút play
                            notificationBuilder
                                    .addAction(R.drawable.baseline_skip_previous_24, "Previous", getPendingIntent(getApplicationContext(), ACTION_PREVIOUS)) // #0
                                    .addAction(R.drawable.baseline_play_arrow_24, "Play", getPendingIntent(getApplicationContext(), ACTION_RESUME)) // #1
                                    .addAction(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(getApplicationContext(), ACTION_NEXT)); // #2
                            notificationBuilder.setContentIntent(pendingIntent);
                        }
                        Notification notification = notificationBuilder.build();
                        startForeground(1, notification);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Implement if needed
                    }
                });
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void getColor(String urlImage) {
        Glide.with(this)
                .asBitmap()
                .load(urlImage)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource).generate(palette -> {
                            int dominantColor = palette.getDominantColor(getResources().getColor(R.color.default_color));
                            float[] hsv = new float[3];
                            Color.colorToHSV(dominantColor, hsv);
                            hsv[2] *= 1.2f;
                            int brighterColor = Color.HSVToColor(hsv);
                            sharedPreferencesManager.saveColorBackgroundState(dominantColor, brighterColor);

                        });
                        return false;
                    }
                })
                .submit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;

        }
        stopUpdatingSeekBar();
    }

    private void startUpdatingSeekBar() {
        seekBarHandler.removeCallbacks(updateSeekBar);
        seekBarHandler.postDelayed(updateSeekBar, 0);
    }

    private void stopUpdatingSeekBar() {
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void sendSeekBarUpdate(int currentTime, int totalTime) {
        Intent intent = new Intent("send_seekbar_update");
        intent.putExtra("current_time", currentTime);
        intent.putExtra("total_time", totalTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int currentTime = mediaPlayer.getCurrentPosition();
                int totalTime = mediaPlayer.getDuration();
                // Cập nhật SeekBar với thời gian hiện tại
                sendSeekBarUpdate(currentTime, totalTime);

                // Lên lịch để cập nhật lại SeekBar sau một khoảng thời gian nhỏ (ví dụ: 1000ms)
                seekBarHandler.postDelayed(this, 1000);
            }
        }
    };

    private final Runnable stopServiceRunnable = this::stopSelf;

    private final Runnable autoNextSongRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentTime = mediaPlayer.getCurrentPosition();
                int totalTime = mediaPlayer.getDuration();
                if (totalTime - currentTime <= 2000) {
                    nextMusic();
                } else {
                    autoNextSongHandler.postDelayed(this, 1000);
                }
            }
        }
    };

    private void sendActionToActivity(int action) {
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", mSong);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", action);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
