package com.example.musichub.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musichub.R;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.constants.Constants;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.uliti.log.LogUtil;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.playlist.Playlist;
import com.example.musichub.model.search.search_multil.SearchMulti;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMultiActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_multi);

        Helper.changeStatusBarColor(this, R.color.black);
        Helper.changeNavigationColor(this, R.color.black, true);

        initViews();
        conFigView();
        initAdapter();
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
    }

    private void initViews() {

    }

    private void conFigView() {

    }

    private void initAdapter() {

    }



}