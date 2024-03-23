package com.example.musichub.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.adapter.VideoAdapter;
import com.example.musichub.model.Song;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestSearchContinuation;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.search.SearchResult;
import com.github.kiulian.downloader.model.search.SearchResultVideoDetails;
import com.github.kiulian.downloader.model.search.field.FormatField;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;

import java.util.ArrayList;
import java.util.List;


public class TestActivity extends AppCompatActivity {

    private YoutubeDownloader downloader = new YoutubeDownloader();
    private VideoAdapter adapter;
    private String searchText;
    private ArrayList<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Khởi tạo Adapter và đặt cho RecyclerView
        adapter = new VideoAdapter(songList, TestActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(TestActivity.this));
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (songList != null) {
                    songList.clear();
                }
                new SearchTask().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchText = s;
                return false;
            }
        });
    }

    private class SearchTask extends AsyncTask<Void, Void, SearchResult> {
        @Override
        protected SearchResult doInBackground(Void... voids) {
            RequestSearchResult request = new RequestSearchResult(searchText)
                    .filter(
                            TypeField.VIDEO,
                            FormatField.HD);

            return downloader.search(request).data();
        }

        @Override
        protected void onPostExecute(SearchResult result) {
            List<SearchResultVideoDetails> videos = result.videos();

            for (SearchResultVideoDetails searchResultVideoDetails : videos) {
                new GetVideoInfoTask().execute(searchResultVideoDetails.videoId());
            }
        }
    }

    private class GetVideoInfoTask extends AsyncTask<String, Void, VideoInfo> {
        @Override
        protected VideoInfo doInBackground(String... videoIds) {
            RequestVideoInfo request = new RequestVideoInfo(videoIds[0]);
            Response<VideoInfo> response = downloader.getVideoInfo(request);
            VideoInfo info = response.data();
            return info;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(VideoInfo info) {
            VideoDetails details = info.details();
            List<VideoWithAudioFormat> videoWithAudioFormats = info.videoWithAudioFormats();

            Song song = new Song();
            song.setId(details.videoId());
            song.setArtist(String.valueOf(details.viewCount()));
            song.setThumb_medium(details.thumbnails().get(0));
            song.setThumb(details.thumbnails().get(0));
            song.setName(details.title());
            song.setLyric(null);

            // Kiểm tra xem song đã tồn tại trong songList chưa
            if (!songList.contains(song)) {
                if (!videoWithAudioFormats.isEmpty()) {
                    song.setLink_audio(videoWithAudioFormats.get(1).url());
                }
                songList.add(song);
                adapter.notifyDataSetChanged();
            }
        }

    }
}
