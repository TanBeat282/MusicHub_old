package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichub.R;
import com.example.musichub.api.categories.Chart;
import com.example.musichub.api.categories.Search;
import com.example.musichub.api.categories.Song;
import com.example.musichub.api.categories.Video;

public class Test2Activity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        Song song = new Song(null, null);
        Video video = new Video(null, null);
        Search search = new Search(null, null);
        Chart chart = new Chart(null, null);
        try {
            Log.d(">>>>>>>>>.", "onCreate: " + chart.getChartHome());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
