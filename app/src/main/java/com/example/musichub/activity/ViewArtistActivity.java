package com.example.musichub.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.song.SongDetail;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewArtistActivity extends AppCompatActivity {
    private Items items;
    private ImageView img_artist;
    private TextView txt_artist;
    private TextView txt_follow;
    private RoundedImageView img_album_song;
    private TextView txtTile;
    private TextView txtArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        // Make the content appear under the status bar
//        Window window = getWindow();
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//        // Set the status bar to be transparent
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
//        }
        setContentView(R.layout.activity_view_artist);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        img_artist = findViewById(R.id.img_artist);
        txt_artist = findViewById(R.id.txt_artist);
        txt_follow = findViewById(R.id.txt_follow);

        img_album_song = findViewById(R.id.img_album_song);
        txtTile = findViewById(R.id.txtTile);
        txtArtist = findViewById(R.id.txtArtist);

        getBundleSong();
    }

    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        } else {
            items = (Items) bundle.getSerializable("items");

            if (items != null) {
                getArtist(items.getArtist().getAlias());
            }
        }

    }

    private void getArtist(String artistId) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories(null, null);
                    Map<String, String> map = songCategories.getArtist(artistId);

                    Call<ResponseBody> call = service.ARTISTS_CALL(artistId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String requestUrl = call.request().url().toString();
                            Log.d(">>>>>>>>>>>>>>>>>>>", " - " + requestUrl);

                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseBody);

                                    if (jsonObject.getInt("err") == 0) {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        String name = data.getString("name");
                                        String totalFollow = data.getString("totalFollow");
                                        String thumbnailM = data.getString("thumbnailM");


                                        //topAlbum
                                        JSONObject topAlbum = data.getJSONObject("topAlbum");
                                        String title = topAlbum.getString("title");
                                        String artistsNames = topAlbum.getString("artistsNames");
                                        String thumbnail = topAlbum.getString("thumbnail");

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                txt_artist.setText(name);
                                                txt_follow.setText(totalFollow);
                                                Glide.with(ViewArtistActivity.this).load(thumbnailM).into(img_artist);

                                                txtTile.setText(title);
                                                txtArtist.setText(artistsNames);
                                                Glide.with(ViewArtistActivity.this).load(thumbnail).into(img_album_song);
                                            }
                                        });
                                    } else {
                                        Log.e("TAG", "Error: " + jsonObject.getString("msg"));
                                    }
                                } catch (Exception e) {
                                    Log.e("TAG", "Error parsing response: " + e.getMessage(), e);
                                }
                            } else {
                                Log.e("TAG", "Response unsuccessful: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e("TAG", "API call failed: " + throwable.getMessage(), throwable);
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