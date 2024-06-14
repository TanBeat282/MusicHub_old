package com.example.musichub.bottomsheet;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musichub.R;
import com.example.musichub.adapter.Artist.SelectArtistAdapter;
import com.example.musichub.model.chart.chart_home.Artists;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Objects;


public class BottomSheetSelectSortSong extends BottomSheetDialogFragment implements SelectArtistAdapter.ArtistItemClickListener {
    private final Context context;
    private final Activity activity;
    private int position;
    private BottomSheetDialog bottomSheetDialog;
    public interface SortOptionListener {
        void onSortOptionSelected(int sortOption);
    }

    private SortOptionListener mListener;
    public BottomSheetSelectSortSong(Context context, Activity activity, int position) {
        this.context = context;
        this.activity = activity;
        this.position = position;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (SortOptionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SortOptionListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_sheet_select_sort_song, null);
        bottomSheetDialog.setContentView(view);

        //1 new 2 pho bien
        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup);
        RadioButton radioNewRelease = bottomSheetDialog.findViewById(R.id.radio_new_release);
        RadioButton radioPopular = bottomSheetDialog.findViewById(R.id.radio_popular);

        // Set the checked state based on the current position
        if (position == 1) {
            Objects.requireNonNull(radioNewRelease).setChecked(true);
        } else if (position == 2) {
            Objects.requireNonNull(radioPopular).setChecked(true);
        }

        // Handle radio button selection changes
        assert radioGroup != null;
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedPosition = 0; // Default position
            if (checkedId == R.id.radio_new_release) {
                selectedPosition = 1;
            } else if (checkedId == R.id.radio_popular) {
                selectedPosition = 2;
            }
            mListener.onSortOptionSelected(selectedPosition);
            bottomSheetDialog.dismiss(); // Dismiss the dialog after selection
        });

        return bottomSheetDialog;
    }


    @Override
    public void onArtistItemClick(boolean isDismiss) {

    }
}
