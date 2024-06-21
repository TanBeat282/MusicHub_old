package com.example.musichub.activity.hub;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.adapter.hub.HubVerticalAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.categories.HubCategories;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.type_adapter_Factory.home.HubSectionTypeAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.hub.Hub;
import com.example.musichub.model.hub.HubSection;
import com.example.musichub.model.hub.SectionHubPlaylist;
import com.example.musichub.model.hub.SectionHubSong;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewHubActivity extends AppCompatActivity {
    private Hub hub;
    private RelativeLayout relative_header;
    private ImageView img_back;
    private TextView txt_name_playlist, txt_view;
    private NestedScrollView nested_scroll;
    private RecyclerView rv_playlist_vertical;
    private ImageView img_playlist;
    private ProgressBar progress_image;

    private final ArrayList<SectionHubSong> sectionHubSongArrayList = new ArrayList<>();
    private final ArrayList<SectionHubPlaylist> sectionHubPlaylists = new ArrayList<>();
    private HubVerticalAdapter hubVerticalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_view_hub);

        initViews();
        initAdapter();
        conFigViews();
        onClick();
        getDataBundle();

    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String encodeId = bundle.getString("encodeId");
            getHub(encodeId);
        }
    }

    private void initViews() {
        relative_header = findViewById(R.id.relative_header);
        img_back = findViewById(R.id.img_back);
        txt_name_playlist = findViewById(R.id.txt_name_playlist);
        txt_view = findViewById(R.id.txt_view);
        nested_scroll = findViewById(R.id.nested_scroll);
        rv_playlist_vertical = findViewById(R.id.rv_playlist_vertical);

        img_playlist = findViewById(R.id.img_playlist);
        progress_image = findViewById(R.id.progress_image);

    }

    private void initAdapter() {
        LinearLayoutManager layoutManagerPlaylist1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_playlist_vertical.setLayoutManager(layoutManagerPlaylist1);
        hubVerticalAdapter = new HubVerticalAdapter(ViewHubActivity.this, ViewHubActivity.this, sectionHubSongArrayList, sectionHubPlaylists);
        rv_playlist_vertical.setAdapter(hubVerticalAdapter);
    }

    private void conFigViews() {
        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Kiểm tra nếu người dùng đã cuộn đến đầu trang
                if (scrollY <= 600) {
                    // Ẩn TextView khi người dùng cuộn trở lại đầu trang
                    txt_name_playlist.setVisibility(View.GONE);
                    txt_view.setVisibility(View.VISIBLE);
                    relative_header.setBackgroundResource(android.R.color.transparent);
                    // Make the content appear under the status bar
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
                    }

                } else if (scrollY >= 800) {
                    // Hiển thị TextView khi người dùng cuộn xuống khỏi đầu trang
                    txt_name_playlist.setVisibility(View.VISIBLE);
                    txt_view.setVisibility(View.GONE);
                    txt_name_playlist.setText(hub.getData().getTitle());
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewHubActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewHubActivity.this, R.color.gray);
                }
            }
        });
    }

    private void onClick() {
        img_back.setOnClickListener(view -> finish());
    }

    private void getHub(String encodeId) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    HubCategories hubCategories = new HubCategories();
                    Map<String, String> map = hubCategories.getHub(encodeId);

                    retrofit2.Call<ResponseBody> call = service.HUB_DETAIL_CALL(map.get("id"), map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getHub " + call.request().url());
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String jsonData = response.body().string();
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    gsonBuilder.registerTypeAdapter(HubSection.class, new HubSectionTypeAdapter());
                                    Gson gson = gsonBuilder.create();

                                    hub = gson.fromJson(jsonData, Hub.class);

                                    if (hub != null && hub.getData() != null) {
                                        ArrayList<HubSection> items = hub.getData().getSections();
                                        for (HubSection item : items) {
                                            if (item instanceof SectionHubPlaylist) {
                                                SectionHubPlaylist sectionHubSong = (SectionHubPlaylist) item;
                                                sectionHubPlaylists.add(sectionHubSong);
                                            } else {
                                                SectionHubSong sectionHubSong = (SectionHubSong) item;
                                                sectionHubSongArrayList.add(sectionHubSong);
                                            }
                                        }
                                        Glide.with(ViewHubActivity.this).load(hub.getData().getThumbnailHasText()).into(img_playlist);
                                        progress_image.setVisibility(View.GONE);
                                        img_playlist.setVisibility(View.VISIBLE);
                                        hubVerticalAdapter.setFilterList(sectionHubSongArrayList, sectionHubPlaylists);
                                    } else {
                                        Log.d("TAG", "No data found in JSON");
                                    }

                                } catch (Exception e) {
                                    Log.e("TAG", "Error: " + e.getMessage(), e);
                                }
                            } else {
                                Log.d("TAG", "Response unsuccessful or empty body");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getHub1111 " + call.request().url());
                        }
                    });

                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG", "Service creation error: " + e.getMessage(), e);
            }
        });
    }

}