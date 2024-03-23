package com.example.musichub.activity;

import static com.example.musichub.service.MyService.ACTION_NEXT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.bottomsheet.BottomSheetInfoSong;
import com.example.musichub.fragment.ContinueSongFragment;
import com.example.musichub.fragment.LyricSongFragment;
import com.example.musichub.fragment.RelatedSongFragment;
import com.example.musichub.helper.Helper;
import com.example.musichub.model.Artist;
import com.example.musichub.model.Song;
import com.example.musichub.service.MyService;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Locale;

public class PlayNowActivity extends AppCompatActivity {
    private TextView txtTitle, txtArtist, tvCurrentTime, tvFullTime, txtPlayform;
    private RoundedImageView imageAlbumArt;
    private ProgressBar progress_image;
    private ImageView imageBackground, imageBack;
    private Song mSong;
    private LottieAnimationView btnPlay;
    private boolean isPlaying;
    private SeekBar playerSeekBar;
    private SharedPreferencesManager sharedPreferencesManager;
    private BottomSheetBehavior bottomSheetBehavior;
    private View layoutPlayer;
    private TabLayout tabLayout;
    private LinearLayout linear_tablayout;
    private LinearLayout linear_bottomsheet;
    private LinearLayout linear_bottom;
    private LinearLayout layoutPlayerTop;
    private ImageView img_play_pause, imageMore;
    private int currentTime, total_time;
    private ArrayList<Song> songArrayList;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            mSong = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int action = bundle.getInt("action_music");
            if (action == MyService.ACTION_START || action == MyService.ACTION_NEXT || action == MyService.ACTION_PREVIOUS) {
                imageAlbumArt.setVisibility(View.GONE);
                progress_image.setVisibility(View.VISIBLE);
                getColorBackground();
            }
            setDataSong();
            setDataSongBottomSheet();
            setStatusButtonPlayOrPause();
        }
    };


    private final BroadcastReceiver seekBarUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentTime = intent.getIntExtra("current_time", 0);
            total_time = intent.getIntExtra("total_time", 0);
            updateSeekBar(currentTime, total_time);
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_now);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        mSong = sharedPreferencesManager.restoreSongState();
        isPlaying = sharedPreferencesManager.restoreIsPlayState();

        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setSelected(true);
        txtArtist = findViewById(R.id.txtArtist);
        txtArtist.setSelected(true);
        imageAlbumArt = findViewById(R.id.imageAlbumArt);
        progress_image = findViewById(R.id.progress_image);
        imageBackground = findViewById(R.id.imageBackground);
        imageBack = findViewById(R.id.imageBack);
        btnPlay = findViewById(R.id.btPlayPause);
        ImageButton btPrevious = findViewById(R.id.btPrevious);
        ImageButton btNext = findViewById(R.id.btNext);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvFullTime = findViewById(R.id.tvFullTime);
        txtPlayform = findViewById(R.id.txtPlayform);
        imageMore = findViewById(R.id.imageMore);

        layoutPlayer = findViewById(R.id.layoutPlayerBottom);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        linear_tablayout = findViewById(R.id.linear_tablayout);
        linear_bottomsheet = findViewById(R.id.bottom_sheet);
        linear_bottom = findViewById(R.id.linear_bottom);

        layoutPlayerTop = layoutPlayer.findViewById(R.id.layoutPlayer);

        LinearLayout linear_play_pause = layoutPlayer.findViewById(R.id.linear_play_pause);
        LinearLayout linear_next = layoutPlayer.findViewById(R.id.linear_next);
        img_play_pause = layoutPlayer.findViewById(R.id.img_play_pause);

        songArrayList = new ArrayList<>();
        tabLayout.setVisibility(View.GONE);
        linear_tablayout.setVisibility(View.VISIBLE);

        bottomSheetBehavior = BottomSheetBehavior.from(linear_bottomsheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        layoutPlayer.setVisibility(View.VISIBLE);

                        tabLayout.setVisibility(View.VISIBLE);
                        linear_tablayout.setVisibility(View.GONE);
                        setDataSongBottomSheet();
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        layoutPlayer.setVisibility(View.GONE);

                        tabLayout.setVisibility(View.GONE);
                        linear_tablayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Đây bạn có thể thực hiện các tương tác khác dựa trên slideOffset nếu cần
            }
        });

        linear_bottomsheet.setOnClickListener(v -> showBottomSheetNowPlaying());
        imageMore.setOnClickListener(v -> showBottomSheetInfo());

        int selectedColor = ContextCompat.getColor(this, R.color.white);
        int unselectedColor = ContextCompat.getColor(this, R.color.colorSecondaryText);
        tabLayout.setTabTextColors(unselectedColor, selectedColor);
        tabLayout.setSelectedTabIndicatorColor(selectedColor);

        viewPager.setAdapter(new TabPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("TIẾP THEO");
                            break;
                        case 1:
                            tab.setText("LỜI NHẠC");
                            break;
                        case 2:
                            tab.setText("LIÊN QUAN");
                            break;
                    }
                }
        ).attach();
        viewPager.setCurrentItem(1);


        getDataSong();
        setDataSong();
        getColorBackground();
        setStatusButtonPlayOrPause();

        btnPlay.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(this, MyService.class)) {
                startService(new Intent(this, MyService.class));
                isPlaying = false;
            }
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
                btnPlay.setMinAndMaxProgress(0.5f, 1.0f); // pause >
                btnPlay.playAnimation();
            } else {
                sendActionToService(MyService.ACTION_RESUME);
                btnPlay.setMinAndMaxProgress(0.0f, 0.5f); // play ||
                btnPlay.playAnimation();
            }
        });

        //bottomsheet
        linear_play_pause.setOnClickListener(v -> {
            if (!Helper.isMyServiceRunning(this, MyService.class)) {
                startService(new Intent(this, MyService.class));
                isPlaying = false;
            }
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
                img_play_pause.setImageResource(R.drawable.baseline_play_arrow_24);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
                img_play_pause.setImageResource(R.drawable.baseline_pause_24);
            }
        });
        linear_next.setOnClickListener(v -> sendActionToService(ACTION_NEXT));

        btPrevious.setOnClickListener(v -> sendActionToService(MyService.ACTION_PREVIOUS));
        btNext.setOnClickListener(v -> sendActionToService(ACTION_NEXT));

        imageBack.setOnClickListener(v -> finish());

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent intent = new Intent(PlayNowActivity.this, MyService.class);
                    intent.putExtra("seek_to_position", progress);
                    startService(intent);
                    Log.d(">>>>>>>>>>>>", "onProgressChanged: " + progress);
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

    private static class TabPagerAdapter extends FragmentStateAdapter {

        public TabPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ContinueSongFragment();
                case 2:
                    return new RelatedSongFragment();
                default:
                    return new LyricSongFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Số lượng tab
        }
    }

    private void getDataSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        Song song = (Song) bundle.getSerializable("song");
        int position_song = bundle.getInt("position_song");
        songArrayList = (ArrayList<Song>) bundle.getSerializable("song_list");
        if (song != null && songArrayList != null) {
            clickStartService(song, position_song, songArrayList);
        }
        setTitleNowPlaying(bundle.getInt("title_now_playing"));
    }

    private void clickStartService(Song song, int position_song, ArrayList<Song> songList) {
        Intent intent = new Intent(this, MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        bundle.putInt("position_song", position_song);
        bundle.putSerializable("song_list", songList);
        intent.putExtras(bundle);

        startService(intent);
    }

    private void showBottomSheetNowPlaying() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void showBottomSheetInfo() {
        BottomSheetInfoSong bottomSheetInfoSong = new BottomSheetInfoSong(PlayNowActivity.this, PlayNowActivity.this, mSong);
        bottomSheetInfoSong.show(getSupportFragmentManager(), bottomSheetInfoSong.getTag());
    }

    private void setStatusButtonPlayOrPause() {
        if (!Helper.isMyServiceRunning(this, MyService.class)) {
            isPlaying = false;
        }
        if (isPlaying) {
            img_play_pause.setImageResource(R.drawable.baseline_pause_24);
        } else {
            img_play_pause.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTitleNowPlaying(int title_now_playing) {
        // set now playing
        if (title_now_playing == 0) {
            txtPlayform.setText("ĐANG PHÁT" + "\n" + "Bảng xếp hạng");
        } else if (title_now_playing == 1) {
            txtPlayform.setText("ĐANG PHÁT" + "\n" + "Tìm kiếm");
        } else if (title_now_playing == 2) {
            txtPlayform.setText("ĐANG PHÁT" + "\n" + "Bài hát liên quan");
        } else {
            txtPlayform.setText("Now Playing");
        }
    }

    private void setDataSong() {
        Glide.with(this)
                .load(mSong.getThumb())
                .into(imageAlbumArt);

        txtTitle.setText(mSong.getName());
        txtArtist.setText(mSong.getArtist());
        updateSeekBar(currentTime, total_time);

        if (!Helper.isMyServiceRunning(this, MyService.class)) {
            isPlaying = false;
        }
        if (isPlaying) {
            btnPlay.setMinAndMaxProgress(0.0f, 0.5f); // play ||
            btnPlay.playAnimation();
        } else {
            btnPlay.setMinAndMaxProgress(0.5f, 1.0f); // pause >
            btnPlay.playAnimation();
        }
        imageAlbumArt.setVisibility(View.VISIBLE);
        progress_image.setVisibility(View.GONE);
    }

    private void setDataSongBottomSheet() {

        TextView txtTitle = layoutPlayer.findViewById(R.id.txtTile);
        TextView txtArtist = layoutPlayer.findViewById(R.id.txtArtist);
        RoundedImageView img_album_song = layoutPlayer.findViewById(R.id.img_album_song);

        Glide.with(this)
                .load(mSong.getThumb_medium())
                .into(img_album_song);

        txtTitle.setText(mSong.getName());
        txtArtist.setText(mSong.getArtist());
    }

    private void setBackground(int color_background, int color_bottomsheet) {
        imageBackground.setBackgroundColor(color_background);
        Window window = getWindow();
        window.setStatusBarColor(color_background);

        ColorStateList colorStateList = ColorStateList.valueOf(color_bottomsheet);
        ViewCompat.setBackgroundTintList(linear_bottom, colorStateList);
        window.setNavigationBarColor(color_bottomsheet);

        ColorStateList colorStateList1 = ColorStateList.valueOf(color_background);
        ViewCompat.setBackgroundTintList(layoutPlayerTop, colorStateList1);

        ColorStateList colorStateList2 = ColorStateList.valueOf(color_bottomsheet);
        ViewCompat.setBackgroundTintList(tabLayout, colorStateList2);

        ColorStateList colorStateList3 = ColorStateList.valueOf(color_background);
        ViewCompat.setBackgroundTintList(linear_bottomsheet, colorStateList3);
    }

    private void getColorBackground() {
        int[] colors = sharedPreferencesManager.restoreColorBackgrounState();
        int color_background = colors[0];
        int color_bottomsheet = colors[1];
        setBackground(color_background, color_bottomsheet);
    }

    private String formatTime(int timeInMillis) {
        String formattedTime;
        int minutes = timeInMillis / 1000 / 60;
        int seconds = (timeInMillis / 1000) % 60;
        formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        return formattedTime;
    }

    private void updateSeekBar(int currentTime, int total_time) {
        playerSeekBar.setMax(total_time);
        playerSeekBar.setProgress(currentTime);
        playerSeekBar.setProgress(currentTime);
        tvCurrentTime.setText(formatTime(currentTime));
        tvFullTime.setText(formatTime((total_time)));
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));
        LocalBroadcastManager.getInstance(this).registerReceiver(seekBarUpdateReceiver, new IntentFilter("send_seekbar_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(seekBarUpdateReceiver);
    }
}