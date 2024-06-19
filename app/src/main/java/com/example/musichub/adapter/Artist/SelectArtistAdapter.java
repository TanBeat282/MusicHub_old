package com.example.musichub.adapter.Artist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Artists;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectArtistAdapter extends RecyclerView.Adapter<SelectArtistAdapter.ViewHolder> {
    private ArrayList<Artists> artistsList;
    private final Context context;
    private final Activity activity;
    private ArtistItemClickListener listener;

    public SelectArtistAdapter(ArrayList<Artists> artistsList, Activity activity, Context context) {
        this.artistsList = artistsList;
        this.activity = activity;
        this.context = context;
    }

    public void setListener(ArtistItemClickListener listener) {
        this.listener = listener;
    }

    public interface ArtistItemClickListener {
        void onArtistItemClick(boolean isDismiss);
    }


    public interface ArtistFollowersCallback {
        void onFollowersFetched(int totalFollow);

        void onError(String error);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Artists> filterList) {
        this.artistsList = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_artist, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Artists artists = artistsList.get(position);

        holder.nameTextView.setText(artists.getName());
        holder.artistTextView.setText("Loading...");

        getArtist(artists.getAlias(), new ArtistFollowersCallback() {
            @Override
            public void onFollowersFetched(int totalFollow) {
                holder.artistTextView.setText(Helper.convertToIntString(totalFollow) + " quan tâm");
            }

            @Override
            public void onError(String error) {
                holder.artistTextView.setText("Error");
            }
        });

        Glide.with(context)
                .load(artists.getThumbnail())
                .into(holder.thumbImageView);
        holder.btn_more.setOnClickListener(view -> {
            if (listener != null) {
                listener.onArtistItemClick(true);
            }

            Intent intent = new Intent(context, ViewArtistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("alias", artists.getAlias());
            intent.putExtras(bundle);

            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArtistItemClick(true);
            }
            Intent intent = new Intent(context, ViewArtistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("alias", artists.getAlias());
            intent.putExtras(bundle);

            context.startActivity(intent);

        });

    }


    @Override
    public int getItemCount() {
        return artistsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView thumbImageView;
        public TextView artistTextView;
        public TextView nameTextView;
        public ImageView btn_more;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumbImageView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            btn_more = itemView.findViewById(R.id.btn_more);
            artistTextView.setSelected(true);
            nameTextView.setSelected(true);
        }
    }

    private void getArtist(String artistId, ArtistFollowersCallback callback) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SongCategories songCategories = new SongCategories();
                    Map<String, String> map = songCategories.getArtist(artistId);

                    Call<ResponseBody> call = service.ARTISTS_CALL(artistId, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseBody);

                                    if (jsonObject.getInt("err") == 0) {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        int totalFollow = data.getInt("totalFollow");
                                        activity.runOnUiThread(() -> callback.onFollowersFetched(totalFollow));
                                    } else {
                                        activity.runOnUiThread(() -> callback.onError("Error: "));
                                    }
                                } catch (Exception e) {
                                    activity.runOnUiThread(() -> callback.onError("Error parsing response: " + e.getMessage()));
                                }
                            } else {
                                activity.runOnUiThread(() -> callback.onError("Response unsuccessful: " + response.message()));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                            activity.runOnUiThread(() -> callback.onError("API call failed: " + throwable.getMessage()));
                        }
                    });
                } catch (Exception e) {
                    activity.runOnUiThread(() -> callback.onError("Error: " + e.getMessage()));
                }
            }

            @Override
            public void onError(Exception e) {
                activity.runOnUiThread(() -> callback.onError("Service creation error: " + e.getMessage()));
            }
        });
    }


}
