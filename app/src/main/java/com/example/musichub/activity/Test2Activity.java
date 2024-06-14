package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichub.R;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.api.categories.VideoCategories;

public class Test2Activity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);


        Button button = findViewById(R.id.btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                call();
//            }
//        });
    }

//    private void call() {
//        try {
//            Song song = new Song(null, null);
//            Map<String, String> map = song.getLyrics("Z7U00WDE");
//
//            ApiService.apiService.L("Z7U00WDE", map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey")).enqueue(new Callback<SongAudio>() {
//                @Override
//                public void onResponse(Call<SongAudio> call, Response<SongAudio> response) {
//                    SongAudio search1 = response.body();
//                    if (search1 != null) {
//                        String requestUrl = call.request().url().toString();
//                        Log.d(">>>>>>>>>>>>>>>>>>>", search1.getMsg() + " - " + requestUrl);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<SongAudio> call, Throwable throwable) {
//                    Log.d(">>>>>>>>>.", "EEEEEEEEEEEEEE: " + "EEEEEEEEEEE");
//                }
//            });
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
