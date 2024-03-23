package com.example.musichub.bottomsheet;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.model.Artist;
import com.example.musichub.model.Song;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class BottomSheetInfoSong extends BottomSheetDialogFragment {
    private final Context context;
    private final Activity activity;
    private Song song;
    private BottomSheetDialog bottomSheetDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private RoundedImageView img_bg;
    private ImageView img_bg2;
    private RelativeLayout layout_info;
    private RoundedImageView imageAlbumArt;
    private TextView txt_name_artist;

    public BottomSheetInfoSong(Context context, Activity activity, Song song) {
        this.context = context;
        this.activity = activity;
        this.song = song;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_sheet_info, null);
        bottomSheetDialog.setContentView(view);

        layout_info = bottomSheetDialog.findViewById(R.id.layout_info);
        img_bg = bottomSheetDialog.findViewById(R.id.img_bg);
        img_bg2 = bottomSheetDialog.findViewById(R.id.img_bg2);
        imageAlbumArt = bottomSheetDialog.findViewById(R.id.imageAlbumArt);
        txt_name_artist = bottomSheetDialog.findViewById(R.id.txt_name_artist);

        sharedPreferencesManager = new SharedPreferencesManager(context);

        getColorBackground();
        getInfoSong(song.getId());

        return bottomSheetDialog;
    }

    private void getInfoSong(String id) {
        OkHttpClient client = new OkHttpClient();

        // Tạo request để gửi lời gọi HTTP GET đến URL
        String url = "https://mp3.zing.vn/xhr/media/get-info?type=audio&id=" + id;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String jsonString = responseBody.string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            JSONObject dataObject = jsonObject.getJSONObject("data");

                            // Lấy mảng nghệ sĩ từ đối tượng data
                            JSONArray artistsArray = dataObject.getJSONArray("artists");

                            // Lặp qua mảng nghệ sĩ và lấy thông tin của nghệ sĩ đầu tiên
                            if (artistsArray.length() > 0) {
                                JSONObject artistObject = artistsArray.getJSONObject(0);

                                Artist artist = new Artist();
                                artist.setId(artistObject.getString("id"));
                                artist.setName(artistObject.getString("name"));
                                artist.setLink(artistObject.getString("link"));
                                artist.setCover(artistObject.getString("cover"));
                                artist.setThumbnail(artistObject.getString("thumbnail"));
                                song.setmArtist(artist);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (song.getmArtist().getCover().startsWith("https://")) {
                                            img_bg.setVisibility(View.VISIBLE);
                                            img_bg2.setVisibility(View.GONE);
                                            Glide.with(context)
                                                    .load(song.getmArtist().getCover())
                                                    .into(img_bg);
                                        } else {
                                            img_bg.setVisibility(View.GONE);
                                            img_bg2.setVisibility(View.VISIBLE);

                                            int[] colors = sharedPreferencesManager.restoreColorBackgrounState();
                                            int color_background = colors[1];
                                            ColorStateList colorStateList = ColorStateList.valueOf(color_background);
                                            ViewCompat.setBackgroundTintList(img_bg2, colorStateList);
                                        }

                                        Glide.with(context)
                                                .load(song.getmArtist().getThumbnail())
                                                .into(imageAlbumArt);

                                        txt_name_artist.setText(song.getmArtist().getName());
                                    }
                                });
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    private void setBackground(int color_background, int color_bottomsheet) {
        Window window = activity.getWindow();
//
        ColorStateList colorStateList3 = ColorStateList.valueOf(color_background);
        ViewCompat.setBackgroundTintList(layout_info, colorStateList3);
    }
    private void getColorBackground() {
        int[] colors = sharedPreferencesManager.restoreColorBackgrounState();
        int color_background = colors[0];
        int color_bottomsheet = colors[1];
        setBackground(color_background, color_bottomsheet);
    }
}
