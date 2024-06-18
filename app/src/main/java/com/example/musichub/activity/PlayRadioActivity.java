package com.example.musichub.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musichub.R;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.categories.RadioCategories;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayRadioActivity extends AppCompatActivity {
    private String endCodeID;
    private PlayerView playerView;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_radio);

        playerView = findViewById(R.id.player_view);
        initializePlayer();
        getBundleSong();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
    }

    private void playStreaming(String url) {
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            endCodeID = bundle.getString("radio_endCodeID");

            if (endCodeID != null) {
                getInfoRadio(endCodeID);
            }
        }
    }

    private void getInfoRadio(String endCodeID) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    RadioCategories radioCategories = new RadioCategories();
                    Map<String, String> map = radioCategories.getInfoRadio(endCodeID);

                    retrofit2.Call<ResponseBody> call = service.INFO_RADIO_CALL(map.get("id"), map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    if (jsonObject.getInt("err") == 0) {
                                        String streamingUrl = jsonObject.getJSONObject("data").getString("streaming");
                                        runOnUiThread(() -> playStreaming(streamingUrl));
                                    }
                                } catch (Exception e) {
                                    Log.e("PlayRadioActivity", "JSON Parsing error: " + e.getMessage(), e);
                                }
                            } else {
                                Log.e("PlayRadioActivity", "Response error: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e("PlayRadioActivity", "getInfoRadio onFailure", throwable);
                        }
                    });
                } catch (Exception e) {
                    Log.e("PlayRadioActivity", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("PlayRadioActivity", "ApiService error: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.play();
        }
    }
}
