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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.activity.ViewPlaylistActivity;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.uliti.CheckIsFile;
import com.example.musichub.helper.uliti.DownloadAudio;
import com.example.musichub.helper.uliti.GetUrlAudioHelper;
import com.example.musichub.helper.uliti.PermissionUtils;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.song.SongAudio;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.makeramen.roundedimageview.RoundedImageView;


public class BottomSheetProfile extends BottomSheetDialogFragment {
    private final Context context;
    private final Activity activity;
    private BottomSheetDialog bottomSheetDialog;

    public BottomSheetProfile(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_sheet_option_song, null);
        bottomSheetDialog.setContentView(view);

        Helper.changeNavigationColor(activity, R.color.gray, true);

        return bottomSheetDialog;
    }
}
