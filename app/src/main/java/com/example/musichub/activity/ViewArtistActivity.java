package com.example.musichub.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.adapter.ArtistsAdapter;
import com.example.musichub.adapter.SingleArtistAdapter;
import com.example.musichub.adapter.TopSongAdapter;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.ApiServiceFactory;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.model.artist.DataArtist;
import com.example.musichub.model.artist.SectionArtistArtist;
import com.example.musichub.model.artist.SectionArtistPlaylist;
import com.example.musichub.model.artist.SectionArtistPlaylistSingle;
import com.example.musichub.model.artist.SectionArtistSong;
import com.example.musichub.model.chart.chart_home.Artists;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.model.chart.home.ItemSlider;
import com.example.musichub.model.playlist.DataPlaylist;
import com.example.musichub.model.song.SongDetail;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewArtistActivity extends AppCompatActivity {
    private Artists artists;
    private DataPlaylist dataPlaylistNewSong;


    private RelativeLayout relative_header;
    private NestedScrollView nested_scroll;
    private TextView txt_name_artist;
    private TextView txt_view;
    private ImageView img_back;
    private ImageView img_more;
    private ImageView img_artist;
    private ProgressBar progress_image;
    private TextView txt_artist;
    private TextView txt_follow;
    private RoundedImageView img_album_song;
    private TextView txtTile;
    private TextView txtArtist;


    private RelativeLayout relative_new_song;


    private RelativeLayout relative_noibat;
    private RecyclerView rv_noibat;

    private RelativeLayout relative_single;
    private RecyclerView rv_single;

    private RelativeLayout relative_album;
    private RecyclerView rv_album;

    private RelativeLayout relative_mv;
    private RecyclerView rv_mv;

    private RelativeLayout relative_playlist;
    private RecyclerView rv_playlist;

    private RelativeLayout relative_xuathientrong;
    private RecyclerView rv_xuathientrong;

    private RelativeLayout relative_other_single;
    private RecyclerView rv_other_single;

    private RelativeLayout relative_info_single;
    private TextView txt_info;
    private TextView txt_name_real;
    private TextView txt_date_birth;
    private TextView txt_country;
    private TextView txt_genre;


    private TopSongAdapter noibatAdapter;
    private ArrayList<Items> itemsArrayListNoiBat;
    private SectionArtistSong sectionArtistSong;



    private SingleArtistAdapter singleArtistAdapter;
    private ArrayList<DataPlaylist> dataSingleArrayList;

    private SingleArtistAdapter playlistAdapter;
    private ArrayList<DataPlaylist> dataPlaylistArrayList;


    private ArtistsAdapter otherSingleAdapter;
    private ArrayList<Artists> artistsArrayList;



    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_view_artist);

        Helper.changeNavigationColor(this, R.color.gray, true);


        img_back = findViewById(R.id.img_back);
        img_more = findViewById(R.id.img_more);

        relative_header = findViewById(R.id.relative_header);
        nested_scroll = findViewById(R.id.nested_scroll);
        txt_name_artist = findViewById(R.id.txt_name_artist);
        txt_view = findViewById(R.id.txt_view);

        img_artist = findViewById(R.id.img_artist);
        progress_image = findViewById(R.id.progress_image);
        txt_artist = findViewById(R.id.txt_artist);
        txt_follow = findViewById(R.id.txt_follow);

        img_album_song = findViewById(R.id.img_album_song);
        txtTile = findViewById(R.id.txtTile);
        txtArtist = findViewById(R.id.txtArtist);

        relative_new_song = findViewById(R.id.relative_new_song);

        relative_noibat = findViewById(R.id.relative_noibat);
        rv_noibat = findViewById(R.id.rv_noibat);

        relative_single = findViewById(R.id.relative_single);
        rv_single = findViewById(R.id.rv_single);


        relative_album = findViewById(R.id.relative_album);
        rv_album = findViewById(R.id.rv_album);

        relative_mv = findViewById(R.id.relative_mv);
        rv_mv = findViewById(R.id.rv_mv);

        relative_playlist = findViewById(R.id.relative_playlist);
        rv_playlist = findViewById(R.id.rv_playlist);

        relative_xuathientrong = findViewById(R.id.relative_xuathientrong);
        rv_xuathientrong = findViewById(R.id.rv_xuathientrong);

        relative_other_single = findViewById(R.id.relative_other_single);
        rv_other_single = findViewById(R.id.rv_other_single);

        relative_info_single = findViewById(R.id.relative_info_single);

        txt_info = findViewById(R.id.txt_info);
        txt_name_real = findViewById(R.id.txt_name_real);
        txt_date_birth = findViewById(R.id.txt_date_birth);
        txt_country = findViewById(R.id.txt_country);
        txt_genre = findViewById(R.id.txt_genre);


        GridLayoutManager layoutManagerNhacMoi = new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false);
        rv_noibat.setLayoutManager(layoutManagerNhacMoi);

        sectionArtistSong = new SectionArtistSong();
        itemsArrayListNoiBat = new ArrayList<>();
        noibatAdapter = new TopSongAdapter(itemsArrayListNoiBat, ViewArtistActivity.this, ViewArtistActivity.this);
        rv_noibat.setAdapter(noibatAdapter);


        LinearLayoutManager layoutManagerOtherSingle = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_other_single.setLayoutManager(layoutManagerOtherSingle);

        artistsArrayList = new ArrayList<>();
        otherSingleAdapter = new ArtistsAdapter(artistsArrayList, ViewArtistActivity.this, ViewArtistActivity.this);
        rv_other_single.setAdapter(otherSingleAdapter);


        LinearLayoutManager layoutManagerSingle = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_single.setLayoutManager(layoutManagerSingle);

        dataSingleArrayList = new ArrayList<>();
        singleArtistAdapter = new SingleArtistAdapter(dataSingleArrayList, ViewArtistActivity.this, ViewArtistActivity.this);
        rv_single.setAdapter(singleArtistAdapter);


        LinearLayoutManager layoutManagerPlaylist = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_playlist.setLayoutManager(layoutManagerPlaylist);

        dataPlaylistArrayList = new ArrayList<>();
        playlistAdapter = new SingleArtistAdapter(dataPlaylistArrayList, ViewArtistActivity.this, ViewArtistActivity.this);
        rv_playlist.setAdapter(playlistAdapter);


        img_back.setOnClickListener(view -> finish());

        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Kiểm tra nếu người dùng đã cuộn đến đầu trang
                if (scrollY <= 600) {
                    // Ẩn TextView khi người dùng cuộn trở lại đầu trang
                    txt_name_artist.setVisibility(View.GONE);
                    txt_view.setVisibility(View.VISIBLE);
                    relative_header.setBackgroundResource(android.R.color.transparent);
                    // Make the content appear under the status bar
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
                    }

                } else if (scrollY >= 800) {
                    // Hiển thị TextView khi người dùng cuộn xuống khỏi đầu trang
                    txt_name_artist.setVisibility(View.VISIBLE);
                    txt_view.setVisibility(View.GONE);
                    txt_name_artist.setText(artists.getName());
                    relative_header.setBackgroundColor(ContextCompat.getColor(ViewArtistActivity.this, R.color.gray));
                    Helper.changeStatusBarColor(ViewArtistActivity.this, R.color.gray);
                }
            }
        });


//        relative_new_song.setOnClickListener(view -> {
//            Intent intent = new Intent(ViewArtistActivity.this, PlayNowActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("song", items);
//            bundle.putInt("position_song", findItemPosition(sectionArtistSong.getItems(),items.getEncodeId()));
//            bundle.putSerializable("song_list", sectionArtistSong.getItems());
//            bundle.putInt("title_now_playing", 0);
//            intent.putExtras(bundle);
//
//            startActivity(intent);
//        });

        getBundleSong();

    }

    private void getBundleSong() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        } else {
            artists = (Artists) bundle.getSerializable("artist");

            if (artists != null) {
                getArtist(artists.getAlias());
            }
        }

    }

    public int findItemPosition(ArrayList<Items> itemsArrayList, String targetEndcodeID) {
        for (int i = 0; i < itemsArrayList.size(); i++) {
            Items item = itemsArrayList.get(i);
            if (item != null && item.getEncodeId() != null) {
                if (targetEndcodeID.equals(item.getEncodeId())) {
                    return i; // Trả về vị trí của phần tử trong mảng
                }
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy phần tử
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

                                        String realname = data.getString("realname");
                                        String biography = data.getString("biography");
                                        String national = data.getString("national");
                                        String birthday = data.getString("birthday");


                                        //topAlbum
                                        JSONObject jsonObjectNewSong = data.optJSONObject("topAlbum");

                                        //song
                                        JSONArray jsonArray = data.optJSONArray("sections");

//                                        //playlist
//                                        JSONArray jsonArrayPlaylist = data.optJSONArray("sections");
//
//                                        SectionArtistPlaylist sectionArtistPlaylist = new SectionArtistPlaylist();
//                                        if (jsonArrayPlaylist != null) {
//
//                                            JSONObject jsonObjectSong = jsonArrayPlaylist.getJSONObject(1);
//                                            sectionArtistPlaylist.setSectionType(jsonObjectSong.getString("sectionType"));
//                                            sectionArtistPlaylist.setViewType(jsonObjectSong.getString("viewType"));
//                                            sectionArtistPlaylist.setTitle(jsonObjectSong.getString("title"));
//                                            sectionArtistPlaylist.setLink(jsonObjectSong.getString("link"));
//                                            sectionArtistPlaylist.setSectionId(jsonObjectSong.getString("sectionId"));
//
//                                            //playlist
//                                            JSONArray innerItemsArray = jsonObjectSong.getJSONArray("items");
//                                            ArrayList<DataPlaylist> dataPlaylistArrayList = new ArrayList<>();
//                                            for (int i = 0; i < innerItemsArray.length(); i++) {
//                                                JSONObject innerItemObject = innerItemsArray.getJSONObject(i);
//                                                DataPlaylist dataPlaylist = DataPlaylist.fromJson(innerItemObject.toString());
//                                                dataPlaylistArrayList.add(dataPlaylist);
//                                            }
//                                            sectionArtistPlaylist.setItems(dataPlaylistArrayList);
//                                        }

                                        //artist
                                        //song
                                        SectionArtistArtist sectionArtistArtist = new SectionArtistArtist();
                                        SectionArtistPlaylistSingle sectionArtistPlaylistSingle = new SectionArtistPlaylistSingle();
                                        SectionArtistPlaylist sectionArtistPlaylist = new SectionArtistPlaylist();


                                        for (int check = 0; check < jsonArray.length(); check++) {
                                            JSONObject jsonObjectSectionType = jsonArray.getJSONObject(check);
                                            String sectionId = jsonObjectSectionType.getString("sectionId");


                                            if (sectionId.equals("aSongs")) {
                                                JSONObject jsonObjectSong = jsonArray.getJSONObject(check);
                                                sectionArtistSong.setSectionType(jsonObjectSong.getString("sectionType"));
                                                sectionArtistSong.setViewType(jsonObjectSong.getString("viewType"));
                                                sectionArtistSong.setTitle(jsonObjectSong.getString("title"));
                                                sectionArtistSong.setLink(jsonObjectSong.getString("link"));
                                                sectionArtistSong.setSectionId(jsonObjectSong.getString("sectionId"));

                                                //items
                                                JSONArray innerItemsArray = jsonObjectSong.getJSONArray("items");
                                                ArrayList<Items> itemsArrayListSong = new ArrayList<>();
                                                for (int i = 0; i < innerItemsArray.length(); i++) {
                                                    JSONObject innerItemObject = innerItemsArray.getJSONObject(i);
                                                    Items items = Items.fromJson(innerItemObject.toString());
                                                    itemsArrayListSong.add(items);
                                                }
                                                sectionArtistSong.setItems(itemsArrayListSong);
                                            }

                                            if (sectionId.equals("aSingle")) {
                                                JSONObject jsonObjectSong = jsonArray.getJSONObject(check);
                                                sectionArtistSong.setSectionType(jsonObjectSong.getString("sectionType"));
                                                sectionArtistSong.setViewType(jsonObjectSong.getString("viewType"));
                                                sectionArtistSong.setTitle(jsonObjectSong.getString("title"));
                                                sectionArtistSong.setLink(jsonObjectSong.getString("link"));
                                                sectionArtistSong.setSectionId(jsonObjectSong.getString("sectionId"));

                                                //items
                                                JSONArray innerItemsArray = jsonObjectSong.getJSONArray("items");
                                                ArrayList<DataPlaylist> dataPlaylistArrayList = new ArrayList<>();
                                                for (int i = 0; i < innerItemsArray.length(); i++) {
                                                    JSONObject innerItemObject = innerItemsArray.getJSONObject(i);
                                                    DataPlaylist dataPlaylist = DataPlaylist.fromJson(innerItemObject.toString());
                                                    dataPlaylistArrayList.add(dataPlaylist);
                                                }
                                                sectionArtistPlaylistSingle.setItems(dataPlaylistArrayList);
                                            }

                                            if (sectionId.equals("aPlaylist")) {
                                                JSONObject jsonObjectSong = jsonArray.getJSONObject(check);
                                                sectionArtistSong.setSectionType(jsonObjectSong.getString("sectionType"));
                                                sectionArtistSong.setViewType(jsonObjectSong.getString("viewType"));
                                                sectionArtistSong.setTitle(jsonObjectSong.getString("title"));
                                                sectionArtistSong.setLink(jsonObjectSong.getString("link"));
                                                sectionArtistSong.setSectionId(jsonObjectSong.getString("sectionId"));

                                                //items
                                                JSONArray innerItemsArray = jsonObjectSong.getJSONArray("items");
                                                ArrayList<DataPlaylist> dataPlaylistArrayList = new ArrayList<>();
                                                for (int i = 0; i < innerItemsArray.length(); i++) {
                                                    JSONObject innerItemObject = innerItemsArray.getJSONObject(i);
                                                    DataPlaylist dataPlaylist = DataPlaylist.fromJson(innerItemObject.toString());
                                                    dataPlaylistArrayList.add(dataPlaylist);
                                                }
                                                sectionArtistPlaylist.setItems(dataPlaylistArrayList);
                                            }


                                            if (sectionId.equals("aReArtist")) {
                                                JSONObject jsonObjectSong = jsonArray.getJSONObject(check);
                                                sectionArtistArtist.setSectionType(jsonObjectSong.getString("sectionType"));
                                                sectionArtistArtist.setViewType(jsonObjectSong.getString("viewType"));
                                                sectionArtistArtist.setTitle(jsonObjectSong.getString("title"));
                                                sectionArtistArtist.setLink(jsonObjectSong.getString("link"));
                                                sectionArtistArtist.setSectionId(jsonObjectSong.getString("sectionId"));

                                                //artist
                                                JSONArray innerItemsArray = jsonObjectSong.getJSONArray("items");
                                                ArrayList<Artists> artistsArrayList = new ArrayList<>();
                                                for (int i = 0; i < innerItemsArray.length(); i++) {
                                                    JSONObject innerItemObject = innerItemsArray.getJSONObject(i);
                                                    Artists artists = Artists.fromJson(innerItemObject.toString());
                                                    artistsArrayList.add(artists);
                                                }
                                                sectionArtistArtist.setItems(artistsArrayList);
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                txt_artist.setText(name);
                                                txt_follow.setText(totalFollow);
                                                Glide.with(ViewArtistActivity.this).load(thumbnailM).into(img_artist);
                                                img_artist.setVisibility(View.VISIBLE);
                                                progress_image.setVisibility(View.GONE);


                                                //topAlbum
                                                if (jsonObjectNewSong != null) {
                                                    if (jsonObjectNewSong.length() > 0) {
                                                        dataPlaylistNewSong = DataPlaylist.fromJson(jsonObjectNewSong.toString());
                                                        relative_new_song.setVisibility(View.VISIBLE);
                                                        txtTile.setText(dataPlaylistNewSong.getTitle());
                                                        txtArtist.setText(dataPlaylistNewSong.getArtistsNames());
                                                        Glide.with(ViewArtistActivity.this).load(dataPlaylistNewSong.getThumbnail()).into(img_album_song);
                                                    } else {
                                                        relative_new_song.setVisibility(View.GONE);
                                                    }
                                                } else {
                                                    relative_new_song.setVisibility(View.GONE);
                                                }

                                                //noi_bat
                                                if (sectionArtistSong != null) {
                                                    relative_noibat.setVisibility(View.VISIBLE);
                                                    noibatAdapter.setFilterList(sectionArtistSong.getItems());
                                                } else {
                                                    relative_new_song.setVisibility(View.GONE);
                                                }

                                                //single
                                                if (sectionArtistPlaylistSingle != null) {
                                                    Log.d(">>>>>>>>>", "run: " + sectionArtistPlaylistSingle.getSectionType());
                                                    relative_single.setVisibility(View.VISIBLE);
                                                    singleArtistAdapter.setFilterList(sectionArtistPlaylistSingle.getItems());
                                                } else {
                                                    relative_single.setVisibility(View.GONE);
                                                }

                                                //playlist
                                                if (sectionArtistPlaylist != null) {
                                                    Log.d(">>>>>>>>>", "run: " + sectionArtistPlaylist.getSectionType());
                                                    relative_playlist.setVisibility(View.VISIBLE);
                                                    playlistAdapter.setFilterList(sectionArtistPlaylist.getItems());
                                                } else {
                                                    relative_playlist.setVisibility(View.GONE);
                                                }


                                                //other_artist
                                                if (sectionArtistArtist != null) {
                                                    Log.d(">>>>>>>>>", "run: " + sectionArtistArtist.getSectionType());
                                                    relative_other_single.setVisibility(View.VISIBLE);
                                                    otherSingleAdapter.setFilterList(sectionArtistArtist.getItems());
                                                } else {
                                                    relative_other_single.setVisibility(View.GONE);
                                                }


                                                CharSequence styledText = Html.fromHtml(biography);
                                                txt_info.setText(styledText);
                                                txt_name_real.setText(realname);
                                                txt_date_birth.setText(birthday);
                                                txt_country.setText(national);
                                                txt_genre.setText(national);
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
                } catch (
                        Exception e) {
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