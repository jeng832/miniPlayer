package com.tweak.just.miniplayer.manager;

import android.content.Context;

import com.tweak.just.miniplayer.model.Music;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private Context context;
    private List<Music> playlist;

    public PlaylistManager(Context context) {
        this.context = context;
        this.playlist = new ArrayList<>();
    }

    public void loadPlaylist() {
        playlist.clear();
        playlist.add(new Music("local_001", "로컬 음악 1", "/storage/emulated/0/Music/song1.mp3"));
        playlist.add(new Music("drive_001", "드라이브 음악 1", "https://drive.google.com/uc?export=download&id=12345"));
    }

    public List<Music> getPlaylist() {
        return playlist;
    }
}
