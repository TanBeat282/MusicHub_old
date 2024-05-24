package com.example.musichub.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.musichub.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.concurrent.TimeUnit;

public class PlayNowVideoActivity extends AppCompatActivity {
    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;
    private SeekBar seekBar;
    private LottieAnimationView btnPlay;
    private TextView txtTitle, txtArtist, tvCurrentTime, tvFullTime, txtPlayform;
    private ImageView imageBackground, imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_now_video);

        playerView = findViewById(R.id.playerView);
        seekBar = findViewById(R.id.playerSeekBar);
        btnPlay = findViewById(R.id.btPlayPause);
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setSelected(true);
        txtArtist = findViewById(R.id.txtArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvFullTime = findViewById(R.id.tvFullTime);
        txtPlayform = findViewById(R.id.txtPlayform);
        imageBackground = findViewById(R.id.imageBackground);
        imageBack = findViewById(R.id.imageBack);

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setUseController(false);
        playerView.setPlayer(exoPlayer);


        btnPlay.setMinAndMaxProgress(0.0f, 0.5f); // play ||
        btnPlay.playAnimation();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        int position_song = bundle.getInt("position_song");

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying) {
                    updateSeekBar();
                }
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                    btnPlay.setMinAndMaxProgress(0.5f, 1.0f); // pause >
                    btnPlay.playAnimation();
                } else {
                    exoPlayer.play();
                    btnPlay.setMinAndMaxProgress(0.0f, 0.5f); // play ||
                    btnPlay.playAnimation();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateSeekBar() {
        seekBar.setMax((int) exoPlayer.getDuration());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    seekBar.postDelayed(this, 1000);
                    updateTimeLabels();
                }
            }
        };
        seekBar.postDelayed(runnable, 1000);
    }

    private void updateTimeLabels() {
        long totalTime = exoPlayer.getDuration();
        long currentTime = exoPlayer.getCurrentPosition();

        String totalTimeFormatted = formatTime(totalTime);
        String currentTimeFormatted = formatTime(currentTime);

        tvFullTime.setText(totalTimeFormatted);
        tvCurrentTime.setText(currentTimeFormatted);
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(long timeInMillis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeInMillis),
                TimeUnit.MILLISECONDS.toMinutes(timeInMillis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }
}
