package com.example.musichub.adapter.Search.search_recommend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.MainActivity;
import com.example.musichub.R;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.activity.hub.ViewHubActivity;
import com.example.musichub.adapter.Search.search_suggestion.SearchSuggestionAdapter;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.search.search_recommend.DataSearchRecommend;
import com.example.musichub.model.search.search_recommend.SearchRecommend;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.ViewHolder> {
    private ArrayList<DataSearchRecommend> dataSearchRecommends;
    private final Context context;
    private final Activity activity;
    private SearchRecommendClickListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<DataSearchRecommend> filterList) {
        this.dataSearchRecommends = filterList;
        notifyDataSetChanged();
    }

    public void setListener(SearchRecommendClickListener listener) {
        this.listener = listener;
    }

    public interface SearchRecommendClickListener {
        void onSearchRecommendClickListener(String keyword);
    }

    public SearchRecommendAdapter(ArrayList<DataSearchRecommend> dataSearchRecommends, Activity activity, Context context) {
        this.dataSearchRecommends = dataSearchRecommends;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DataSearchRecommend dataSearchRecommend = dataSearchRecommends.get(position);


        holder.txt_keyword.setText(dataSearchRecommend.getKeyword());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dataSearchRecommend.getLink().isEmpty()) {
                    Intent intent = new Intent(context, ViewHubActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("encodeId", Helper.extractEndCodeID(dataSearchRecommend.getLink()));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    listener.onSearchRecommendClickListener(dataSearchRecommend.getKeyword());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSearchRecommends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_keyword;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_keyword = itemView.findViewById(R.id.txt_keyword);
        }
    }

}
