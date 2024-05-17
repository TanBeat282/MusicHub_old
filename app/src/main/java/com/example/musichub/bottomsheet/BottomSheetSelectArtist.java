package com.example.musichub.bottomsheet;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.adapter.SelectArtistAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.uliti.CheckIsFile;
import com.example.musichub.helper.uliti.DownloadAudio;
import com.example.musichub.helper.uliti.GetUrlAudioHelper;
import com.example.musichub.helper.uliti.PermissionUtils;
import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.song.SongAudio;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BottomSheetSelectArtist extends BottomSheetDialogFragment {
    private final Context context;
    private final Activity activity;
    private ArrayList<Artists> artistsArrayList;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView rv_select_artist;
    private SelectArtistAdapter selectArtistAdapter;

    public BottomSheetSelectArtist(Context context, Activity activity, ArrayList<Artists> artistsArrayList) {
        this.context = context;
        this.activity = activity;
        this.artistsArrayList = artistsArrayList;
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_sheet_select_artist, null);
        bottomSheetDialog.setContentView(view);

        rv_select_artist = bottomSheetDialog.findViewById(R.id.rv_select_artist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_select_artist.setLayoutManager(linearLayoutManager);

        selectArtistAdapter = new SelectArtistAdapter(artistsArrayList, activity, context);
        rv_select_artist.setAdapter(selectArtistAdapter);

        selectArtistAdapter.setFilterList(artistsArrayList);

        return bottomSheetDialog;
    }
}
