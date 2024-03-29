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
import com.example.musichub.model.song.SongDetail;
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
    private SongDetail song;
    private BottomSheetDialog bottomSheetDialog;
    private SharedPreferencesManager sharedPreferencesManager;
    private RoundedImageView img_bg;
    private RelativeLayout layout_info;
    private RoundedImageView imageAlbumArt;
    private TextView txt_name_artist;

    public BottomSheetInfoSong(Context context, Activity activity, SongDetail song) {
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
        imageAlbumArt = bottomSheetDialog.findViewById(R.id.imageAlbumArt);
        txt_name_artist = bottomSheetDialog.findViewById(R.id.txt_name_artist);

        sharedPreferencesManager = new SharedPreferencesManager(context);

        getColorBackground();

        Glide.with(context)
                .load(song.getData().getArtists().get(0).getThumbnailM())
                .into(img_bg);

//        int color = getResources().getColor(R.color.colorPrimary);
//        img_bg.setImageResource(color);

        Glide.with(context)
                .load(song.getData().getArtists().get(0).getThumbnailM())
                .into(imageAlbumArt);

        txt_name_artist.setText(song.getData().getArtists().get(0).getName());

        return bottomSheetDialog;
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
