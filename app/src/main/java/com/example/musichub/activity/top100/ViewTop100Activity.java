package com.example.musichub.activity.top100;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.hub.ViewHubActivity;
import com.example.musichub.adapter.hub.HubVerticalAdapter;
import com.example.musichub.adapter.top100.Top100Adapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.categories.ChartCategories;
import com.example.musichub.api.categories.HubCategories;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.type_adapter_Factory.home.HubSectionTypeAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.hub.Hub;
import com.example.musichub.model.hub.HubSection;
import com.example.musichub.model.hub.SectionHubPlaylist;
import com.example.musichub.model.hub.SectionHubSong;
import com.example.musichub.model.playlist.DataPlaylist;
import com.example.musichub.model.top100.DataTop100;
import com.example.musichub.model.top100.Top100;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewTop100Activity extends AppCompatActivity {
    private Top100 top100;
    private RelativeLayout relative_header;
    private ImageView img_back;
    private TextView txt_name_playlist, txt_view;
    private NestedScrollView nested_scroll;
    private RecyclerView rv_playlist_vertical;
    private ImageView img_playlist;
    private ProgressBar progress_image;

    private final ArrayList<DataTop100> dataTop100ArrayList = new ArrayList<>();
    private Top100Adapter top100Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_view_top100);

        initViews();
        initAdapter();
        conFigViews();
        onClick();
        getTop100();
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
        top100Adapter = new Top100Adapter(dataTop100ArrayList, ViewTop100Activity.this, ViewTop100Activity.this);
        rv_playlist_vertical.setAdapter(top100Adapter);
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
                    txt_name_playlist.setText("Top 100");
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewTop100Activity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewTop100Activity.this, R.color.gray);
                }
            }
        });
    }

    private void onClick() {
        img_back.setOnClickListener(view -> finish());
    }

    private void getTop100() {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    ChartCategories chartCategories = new ChartCategories();
                    Map<String, String> map = chartCategories.getTop100();

                    retrofit2.Call<Top100> call = service.TOP100_CALL(map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<Top100>() {
                        @Override
                        public void onResponse(Call<Top100> call, Response<Top100> response) {
                            Log.d(">>>>>>>>>>>>>>>>>>", "getHub " + call.request().url());
                            if (response.isSuccessful() && response.body() != null) {
                                top100 = response.body();
                                runOnUiThread(() -> {
                                    Glide.with(ViewTop100Activity.this).load("https://fuxwithit.com/wp-content/uploads/2015/12/Fuxwithit-100-logo-final.png").into(img_playlist);
                                    progress_image.setVisibility(View.GONE);
                                    img_playlist.setVisibility(View.VISIBLE);
                                    top100Adapter.setFilterList(top100.getData());
                                });
                            } else {
                                Log.d("TAG", "Response unsuccessful or empty body");
                            }
                        }

                        @Override
                        public void onFailure(Call<Top100> call, Throwable throwable) {
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