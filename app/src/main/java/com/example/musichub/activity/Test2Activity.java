package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichub.R;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class Test2Activity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        String youtubeLink = "WX7dUj14Z00";

        new YouTubeExtractor(this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 22;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    Log.d(">>>>>>>>>>>>>>>>>>>>>>", "onExtractionComplete: "+downloadUrl);
                }
            }
        }.extract(youtubeLink, true, true); // Truyền giá trị true cho cả hai tham số audioOnly và meta
    }
}
