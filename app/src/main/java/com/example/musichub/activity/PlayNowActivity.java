package com.example.musichub.activity;

import static com.example.musichub.service.MyService.ACTION_NEXT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.bottomsheet.BottomSheetInfoSong;
import com.example.musichub.fragment.ContinueSongFragment;
import com.example.musichub.fragment.LyricSongFragment;
import com.example.musichub.fragment.RelatedSongFragment;
import com.example.musichub.helper.uliti.GetUrlAudioHelper;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart_home.Items;
import com.example.musichub.model.song.SongAudio;
import com.example.musichub.model.song.SongDetail;
import com.example.musichub.service.MyService;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayNowActivity extends AppCompatActivity {
    private long downloadID;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 101;
    private TextView txtTitle, txtArtist, tvCurrentTime, tvFullTime, txtPlayform;
    private RoundedImageView imageAlbumArt;
    private ProgressBar progress_image;
    private ImageView imageBackground, imageBack;
    private Items items;
    private SongDetail songDetail;
    private LottieAnimationView btnPlay;
    private boolean isPlaying;
    private SeekBar playerSeekBar;
    private SharedPreferencesManager sharedPreferencesManager;
    private BottomSheetBehavior bottomSheetBehavior;
    private View layoutPlayer;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LinearLayout linear_tablayout;
    private LinearLayout linear_bottomsheet;
    private LinearLayout linear_bottom;
    private LinearLayout layoutPlayerTop;
    private LinearLayout btn_down_audio;
    private ImageView img_play_pause, imageMore;
    private ImageView img_download_audio;
    private TextView txt_download_audio;
    private TextView txt_view_audio, txt_like, txt_comment;
    private int currentTime, total_time;
    private ArrayList<Items> songArrayList;
    private GetUrlAudioHelper getUrlAudioHelper;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            items = (Items) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int action = bundle.getInt("action_music");
            if (action == MyService.ACTION_START || action == MyService.ACTION_NEXT || action == MyService.ACTION_PREVIOUS) {
                imageAlbumArt.setVisibility(View.GONE);
                progress_image.setVisibility(View.VISIBLE);
                getColorBackground();
            }
            getSongDetail(items.getEncodeId(), new SongdetailCallback() {
                @Override
                public void onSuccess(SongDetail songDetail1) {
                    songDetail = songDetail1;
                    setDataSong();
                    setDataSongBottomSheet();
                    setStatusButtonPlayOrPause();
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
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

    @SuppressLint({"MissingInflatedId", "UnspecifiedRegisterReceiverFlag", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_now);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        items = sharedPreferencesManager.restoreSongState();
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
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        linear_tablayout = findViewById(R.id.linear_tablayout);
        linear_bottomsheet = findViewById(R.id.bottom_sheet);
        linear_bottom = findViewById(R.id.linear_bottom);

        btn_down_audio = findViewById(R.id.btn_down_audio);
        img_download_audio = findViewById(R.id.img_download_audio);
        txt_download_audio = findViewById(R.id.txt_download_audio);

        txt_view_audio = findViewById(R.id.txt_view_audio);
        txt_like = findViewById(R.id.txt_like);
        txt_comment = findViewById(R.id.txt_comment);

        layoutPlayerTop = layoutPlayer.findViewById(R.id.layoutPlayer);

        LinearLayout linear_play_pause = layoutPlayer.findViewById(R.id.linear_play_pause);
        LinearLayout linear_next = layoutPlayer.findViewById(R.id.linear_next);
        img_play_pause = layoutPlayer.findViewById(R.id.img_play_pause);

        getUrlAudioHelper = new GetUrlAudioHelper();
        songArrayList = new ArrayList<>();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
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
                        viewPager.setVisibility(View.VISIBLE);
                        linear_tablayout.setVisibility(View.GONE);
                        setDataSongBottomSheet();
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        layoutPlayer.setVisibility(View.GONE);

                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
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


        getBundleSong();
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

        btn_down_audio.setOnClickListener(view -> {
            if (isFileDownloaded(items.getTitle() + " - " + items.getArtistsNames() + ".mp3")) {
                deleteFileIfExists(items.getTitle() + " - " + items.getArtistsNames() + ".mp3");
            } else {
                xinquyen();
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
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    // download mp3
    // check is download
    private boolean isFileDownloaded(String fileName) {
        File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File destinationFile = new File(musicFolder, fileName);
        return destinationFile.exists();
    }


    // delete file
    @SuppressLint("SetTextI18n")
    private void deleteFileIfExists(String fileName) {
        File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File destinationFile = new File(musicFolder, fileName);
        if (destinationFile.exists()) {
            boolean deleted = destinationFile.delete();
            if (deleted) {
                txt_download_audio.setText("Tải xuống");
                img_download_audio.setImageResource(R.drawable.ic_download);
            } else {
                Toast.makeText(PlayNowActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                // Xóa file không thành công
            }
        } else {
            Toast.makeText(PlayNowActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
        }
    }

    // request permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage("Song.getLink_audio()");
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền ghi vào bộ nhớ để tải tệp về.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // request permissions
    private void xinquyen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION);
        } else {
            getUrlAudioHelper.getSongAudio(items.getEncodeId(), new GetUrlAudioHelper.SongAudioCallback() {
                @Override
                public void onSuccess(SongAudio songAudio) {
                    downloadImage(songAudio.getData().getLow());
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }
    }

    //download
    private void downloadImage(String url) {
        File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if (!musicFolder.exists()) {
            musicFolder.mkdirs();
        }

        String fileName = items.getTitle() + " - " + items.getArtistsNames() + ".mp3";
        File destinationFile = new File(musicFolder, fileName);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setDescription("Đang lưu...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION) // Đặt VISIBILITY_VISIBLE
                .setDestinationUri(Uri.fromFile(destinationFile));

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
    }


    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadID) {
                Toast.makeText(PlayNowActivity.this, "Tải thành công", Toast.LENGTH_SHORT).show();
                txt_download_audio.setText("Đã tải xuống");
                img_download_audio.setImageResource(R.drawable.ic_file_download_done);
            }
        }
    };

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

    public interface SongdetailCallback {
        void onSuccess(SongDetail songDetail1);

        void onFailure(Throwable throwable);
    }

    private void getSongDetail(String encodeId, SongdetailCallback songdetailCallback) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getDetail(encodeId);


                    retrofit2.Call<SongDetail> call = service.SONG_DETAIL_CALL(encodeId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<SongDetail>() {
                        @Override
                        public void onResponse(Call<SongDetail> call, Response<SongDetail> response) {
                            if (response.isSuccessful()) {
                                SongDetail songDetail1 = response.body();
                                if (songDetail1 != null) {
                                    if (songDetail1.getErr() != -201) {
                                        songdetailCallback.onSuccess(songDetail1);
                                    } else {
                                        songdetailCallback.onFailure(new RuntimeException("Error -201: Unable to get audio URL"));
                                    }
                                } else {
                                    songdetailCallback.onFailure(new RuntimeException("Null response from server"));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SongDetail> call, Throwable throwable) {
                            songdetailCallback.onFailure(throwable);
                        }
                    });
                } catch (Exception e) {
                    songdetailCallback.onFailure(e);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            getSongDetail(items.getEncodeId(), new SongdetailCallback() {
                @Override
                public void onSuccess(SongDetail songDetail1) {
                    songDetail = songDetail1;
                    setDataSong();
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        } else {

            items = (Items) bundle.getSerializable("song");
            int position_song = bundle.getInt("position_song");
            songArrayList = (ArrayList<Items>) bundle.getSerializable("song_list");
            setTitleNowPlaying(bundle.getInt("title_now_playing"));

            if (items != null && songArrayList != null) {
                getSongDetail(items.getEncodeId(), new SongdetailCallback() {
                    @Override
                    public void onSuccess(SongDetail songDetail1) {
                        songDetail = songDetail1;
                        setDataSong();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
                clickStartService(items, position_song, songArrayList);
            }
        }

    }

    private void clickStartService(Items song, int position_song, ArrayList<Items> songList) {
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
        BottomSheetInfoSong bottomSheetInfoSong = new BottomSheetInfoSong(PlayNowActivity.this, PlayNowActivity.this, songDetail);
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

    @SuppressLint("SetTextI18n")
    private void setDataSong() {
        Glide.with(this)
                .load(items.getThumbnailM())
                .into(imageAlbumArt);

        txtTitle.setText(items.getTitle());
        txtArtist.setText(items.getArtistsNames());

        txt_view_audio.setText(String.valueOf(convertToIntString(songDetail.getData().getListen())));
        txt_like.setText(String.valueOf(convertToIntString(songDetail.getData().getLike())));
        txt_comment.setText(String.valueOf(convertToIntString(songDetail.getData().getComment())));

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

        if (isFileDownloaded(items.getTitle() + " - " + items.getArtistsNames() + ".mp3")) {
            txt_download_audio.setText("Đã tải xuống");
            img_download_audio.setImageResource(R.drawable.ic_file_download_done);
        } else {
            txt_download_audio.setText("Tải xuống");
            img_download_audio.setImageResource(R.drawable.ic_download);
        }
    }

    public String convertToIntString(int number) {
        String numberString = String.valueOf(number);
        int length = numberString.length();

        if (length == 4) {
            return numberString.charAt(0) + "." + numberString.charAt(1) + "K";
        } else if (length == 5) {
            return numberString.charAt(0) + numberString.charAt(1) + "K";
        } else if (length == 6) {
            return numberString.charAt(0) + numberString.charAt(1) + numberString.charAt(2) + "K";
        } else if (length == 7) {
            return numberString.charAt(0) + "." + numberString.charAt(1) + "M";
        } else if (length == 8) {
            return numberString.charAt(0) + numberString.charAt(1) + "M";
        } else if (length == 9) {
            return numberString.charAt(0) + numberString.charAt(1) + numberString.charAt(2) + "M";
        } else {
            return numberString;
        }
    }


    private void setDataSongBottomSheet() {

        TextView txtTitle = layoutPlayer.findViewById(R.id.txtTile);
        TextView txtArtist = layoutPlayer.findViewById(R.id.txtArtist);
        txtTitle.setSelected(true);
        txtTitle.setSelected(true);
        RoundedImageView img_album_song = layoutPlayer.findViewById(R.id.img_album_song);

        Glide.with(this)
                .load(items.getThumbnail())
                .into(img_album_song);

        txtTitle.setText(items.getTitle());
        txtArtist.setText(items.getArtistsNames());
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
        unregisterReceiver(onDownloadComplete);
    }
}