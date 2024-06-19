package com.example.musichub.fragment.search_multi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musichub.R;
import com.example.musichub.activity.ViewArtistActivity;
import com.example.musichub.api.ApiService;
import com.example.musichub.api.categories.SearchCategories;
import com.example.musichub.api.categories.SongCategories;
import com.example.musichub.api.service.ApiServiceFactory;
import com.example.musichub.constants.Constants;
import com.example.musichub.helper.ui.Helper;
import com.example.musichub.helper.uliti.log.LogUtil;
import com.example.musichub.model.search.search_multil.SearchMulti;
import com.example.musichub.model.search.search_multil.SearchMultiDataTop;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllSeachMultiFragment extends Fragment {
    private static final String ARG_DATA = "query";
    private static final String allowCorrect = "1";
    private String query;

    private SearchMulti searchMulti;

    private LinearLayout linear_top;
    private LinearLayout linear_info;
    private RoundedImageView img_avatar;
    private TextView tv_name;
    private TextView txt_followers;


    private final BroadcastReceiver broadcastReceiverSearchMulti = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            query = bundle.getString("query");
            searchMulti(query);
        }
    };

    public static AllSeachMultiFragment newInstance(String data) {
        AllSeachMultiFragment fragment = new AllSeachMultiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_DATA);
            searchMulti(query);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_seach_multi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initAdapter();
        onClick();
    }

    private void initView(View view) {
        linear_top = view.findViewById(R.id.linear_top);
        linear_info = view.findViewById(R.id.linear_info);
        img_avatar = view.findViewById(R.id.img_avatar);
        tv_name = view.findViewById(R.id.tv_name);
        txt_followers = view.findViewById(R.id.txt_followers);
    }

    private void initAdapter() {

    }

    private void onClick() {
        linear_info.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), ViewArtistActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("alias", searchMulti.getData().getTop().getAlias());
            intent.putExtras(bundle);

            requireContext().startActivity(intent);
        });
    }

    public interface ArtistFollowersCallback {
        void onFollowersFetched(int totalFollow);

        void onError(String error);
    }

    @SuppressLint("SetTextI18n")
    private void setDataTop(SearchMultiDataTop searchMultiDataTop) {
        if (!isAdded() || getActivity() == null) {
            return;
        }
        tv_name.setText(searchMultiDataTop.getName());
        Glide.with(requireContext()).load(searchMultiDataTop.getThumbnail()).into(img_avatar);

        txt_followers.setText("Loading...");
        getFollowerOfArtist(searchMultiDataTop.getAlias(), new ArtistFollowersCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFollowersFetched(int totalFollow) {
                txt_followers.setText(Helper.convertToIntString(totalFollow) + " quan tâm");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(String error) {
                txt_followers.setText("Error");
            }
        });
    }

    private void searchMulti(String query) {
        ApiServiceFactory.createServiceAsync(new ApiServiceFactory.ApiServiceCallback() {
            @Override
            public void onServiceCreated(ApiService service) {
                try {
                    SearchCategories searchCategories = new SearchCategories();
                    Map<String, String> map = searchCategories.getSearchMulti(query);

                    retrofit2.Call<SearchMulti> call = service.SEARCH_MULTI_CALL(map.get("q"), allowCorrect, map.get("sig"), map.get("ctime"), map.get("version"), map.get("apiKey"));
                    call.enqueue(new Callback<SearchMulti>() {
                        @Override
                        public void onResponse(@NonNull Call<SearchMulti> call, @NonNull Response<SearchMulti> response) {
                            LogUtil.d(Constants.TAG, "searchMulti: " + call.request().url());
                            if (response.isSuccessful()) {
                                searchMulti = response.body();
                                if (searchMulti != null && searchMulti.getErr() == 0) {
                                    setDataTop(searchMulti.getData().getTop());
                                } else {
                                    Log.d("TAG", "Error: ");
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<SearchMulti> call, @NonNull Throwable throwable) {
                            LogUtil.d(Constants.TAG, "searchMulti2: " + call.request().url());
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Error: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void getFollowerOfArtist(String artistId, ArtistFollowersCallback callback) {
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
                                        requireActivity().runOnUiThread(() -> callback.onFollowersFetched(totalFollow));
                                    } else {
                                        requireActivity().runOnUiThread(() -> callback.onError("Error: "));
                                    }
                                } catch (Exception e) {
                                    requireActivity().runOnUiThread(() -> callback.onError("Error parsing response: " + e.getMessage()));
                                }
                            } else {
                                requireActivity().runOnUiThread(() -> callback.onError("Response unsuccessful: " + response.message()));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                            requireActivity().runOnUiThread(() -> callback.onError("API call failed: " + throwable.getMessage()));
                        }
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> callback.onError("Error: " + e.getMessage()));
                }
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> callback.onError("Service creation error: " + e.getMessage()));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchMulti != null) {
            searchMulti = null;
        }
        searchMulti(query);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiverSearchMulti, new IntentFilter("search_query"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiverSearchMulti);
    }
}