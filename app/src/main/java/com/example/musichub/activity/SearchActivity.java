package com.example.musichub.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.adapter.SearchAdapter;
import com.example.musichub.model.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchActivity extends AppCompatActivity {
    private String textSearch;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout txtNoData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        ArrayList<Song> songList = new ArrayList<>();

        SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        txtNoData = findViewById(R.id.txtNoData);
        TextView txtNoMusic = findViewById(R.id.txtNoMusic);

        // Khởi tạo RecyclerView và Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter

        adapter = new SearchAdapter(songList, this);
        recyclerView.setAdapter(adapter);

        txtNoData.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        txtNoMusic.setText("Không có bài hát nào ở đây");

        fetchDataMusic();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch = query;
                fetchDataMusic();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch = newText;
                fetchDataMusic();
                return true;
            }
        });
    }
    private void fetchDataMusic() {
        if (textSearch == null || textSearch.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            OkHttpClient client = new OkHttpClient();

            // Tạo request để gửi lời gọi HTTP GET đến URL
            String url = "http://ac.mp3.zing.vn/complete?type=artist,song,key,code&num=50&query=" + textSearch;
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
                                JSONArray data = jsonObject.getJSONArray("data");
                                JSONObject firstObject = data.getJSONObject(0);
                                JSONArray songArray = firstObject.getJSONArray("song");

                                ArrayList<Song> filteredSongList = new ArrayList<>();

                                for (int i = 0; i < songArray.length(); i++) {

                                    JSONObject songObject = songArray.getJSONObject(i);

                                    Song song = new Song();

                                    String urlTemp = songObject.getString("thumb");
                                    song.setThumb_medium("https://photo-resize-zmp3.zmdcdn.me/w94_r1x1_png/" + urlTemp);
                                    song.setThumb("https://photo-resize-zmp3.zmdcdn.me/w480_r1x1_png/" + urlTemp);
                                    song.setArtist(songObject.getString("artist"));
                                    song.setId(songObject.getString("id"));
                                    song.setName(songObject.getString("name"));

//                                    getLinkAudioSong(song.getCode(), new LinkAudioCallback() {
//                                        @Override
//                                        public void onLinkReceived(String link) {
//                                            if (link != null) {
//                                                song.setLink_audio(link);
//                                            } else {
//                                              runOnUiThread(new Runnable() {
//                                                  @Override
//                                                  public void run() {
//                                                      Toast.makeText(SearchActivity.this, "Lỗi truy vấn.", Toast.LENGTH_SHORT).show();
//                                                  }
//                                              });
//                                            }
//                                        }
//                                    });

                                    filteredSongList.add(song);
                                }
                                runOnUiThread(() -> adapter.setFillterList(filteredSongList));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }
    public interface LinkAudioCallback {
        void onLinkReceived(String link);
    }

//    private void getLinkAudioSong(String code_song, final LinkAudioCallback callback) {
//        OkHttpClient client = new OkHttpClient();
//
//        // Tạo request để gửi lời gọi HTTP GET đến URL
//        String url = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + code_song;
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//                callback.onLinkReceived(null); // Gọi callback với giá trị null khi xảy ra lỗi
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    ResponseBody responseBody = response.body();
//                    if (responseBody != null) {
//                        String jsonString = responseBody.string();
//                        try {
//                            JSONObject jsonObject = new JSONObject(jsonString);
//                            JSONObject dataObject = jsonObject.getJSONObject("data");
//                            JSONObject sourceObject = dataObject.getJSONObject("source");
//                            String link128 = sourceObject.getString("128");
//                            callback.onLinkReceived(link128); // Gọi callback với giá trị link128 khi thành công
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            callback.onLinkReceived(null); // Gọi callback với giá trị null khi có lỗi xử lý JSON
//                        }
//                    }
//                }
//            }
//        });
//    }
}