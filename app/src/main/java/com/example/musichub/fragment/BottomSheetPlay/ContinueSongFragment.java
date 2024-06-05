package com.example.musichub.fragment.BottomSheetPlay;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musichub.R;
import com.example.musichub.adapter.Song.SongAllAdapter;
import com.example.musichub.model.chart.chart_home.Items;
import com.example.musichub.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;


public class ContinueSongFragment extends Fragment {
    private RecyclerView recyclerView;
    private SongAllAdapter adapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private ArrayList<Items> songArrayList;
    private int positionSong = -1;
    private Items song;
    private Items items;
    private int currentTime, total_time;
    private boolean isPlaying;
    private int action;

//    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = intent.getExtras();
//            if (bundle == null) {
//                return;
//            }
//            song = (Items) bundle.get("object_song");
//            isPlaying = bundle.getBoolean("status_player");
//            action = bundle.getInt("action_music");
//            checkIsPlaying(song);
//        }
//    };

    public ContinueSongFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_continue_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);

//        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
////        songArrayList = sharedPreferencesManager.restoreSongArrayList();
//        items = sharedPreferencesManager.restoreSongState();
//        positionSong = sharedPreferencesManager.restoreSongPosition();
//        // Khoi tạo RecyclerView và Adapter
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
////         Khoi tạo Adapter
//        adapter = new SongAllAdapter(songArrayList, requireActivity(), requireContext());
//        recyclerView.setAdapter(adapter);
//        checkIsPlaying(items);

    }
//
//    private void checkIsPlaying(Items items) {
//        if (items == null || songArrayList == null) {
//            return;
//        }
//
//        String currentEncodeId = items.getEncodeId();
//        if (currentEncodeId != null && !currentEncodeId.isEmpty()) {
//            for (Items song : songArrayList) {
//                if (currentEncodeId.equals(song.getEncodeId())) {
//                    adapter.updatePlayingStatus(currentEncodeId);
//                    break;
//                }
//            }
//        }
//    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
//        // Xóa dữ liệu cũ và lấy dữ liệu mới từ SharedPreferences
//        songArrayList.clear(); // Xóa dữ liệu cũ
//        songArrayList.addAll(sharedPreferencesManager.restoreSongArrayList()); // Lấy dữ liệu mới
//        items = sharedPreferencesManager.restoreSongState();
//        positionSong = sharedPreferencesManager.restoreSongPosition();
//        // Đăng ký BroadcastReceiver để nhận dữ liệu từ Activity
//        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));
//        // Cập nhật RecyclerView
//        adapter.notifyDataSetChanged();
//        checkIsPlaying(items);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký BroadcastReceiver khi Fragment bị hủy
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }
}