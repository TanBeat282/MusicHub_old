package com.example.musichub.helper;

import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;

public class CustomLinearSmoothScroller extends LinearSmoothScroller {
    public CustomLinearSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }
}
