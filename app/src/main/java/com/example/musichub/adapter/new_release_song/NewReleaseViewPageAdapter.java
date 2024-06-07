package com.example.musichub.adapter.new_release_song;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.musichub.fragment.NewReleaseSong.AlbumFragment;
import com.example.musichub.fragment.NewReleaseSong.SongFragment;

public class NewReleaseViewPageAdapter extends FragmentPagerAdapter {

    public NewReleaseViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SongFragment();
            case 1:
                return new AlbumFragment();
            default:
                return new SongFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Bài hát";
                break;
            case 1:
                title = "Album";
        }
        return title;
    }

}
