package com.tweak.just.miniplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.tweak.just.miniplayer.manager.CacheManager;
import com.tweak.just.miniplayer.manager.PlaylistManager;
import com.tweak.just.miniplayer.model.Music;

import java.io.IOException;

public class MusicPlayerService extends Service {

    private MediaPlayer mediaPlayer;
    private PlaylistManager playlistManager;
    private CacheManager cacheManager;
    private int currentTrackIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        playlistManager = new PlaylistManager(getApplicationContext());
        cacheManager = new CacheManager(getApplicationContext());
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(mp -> playNext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "ACTION_PLAY":
                    playMusic();
                    break;
                case "ACTION_PAUSE":
                    pauseMusic();
                    break;
                case "ACTION_NEXT":
                    playNext();
                    break;
                case "ACTION_PREV":
                    playPrevious();
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMusic() {
        if (playlistManager.getPlaylist().isEmpty()) return;

        Music currentMusic = playlistManager.getPlaylist().get(currentTrackIndex);
        String musicPath = cacheManager.getCachedFilePath(currentMusic);
        if (musicPath == null) {
            musicPath = currentMusic.getPath();
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            cacheManager.cacheMusicFile(currentMusic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void playNext() {
        currentTrackIndex++;
        if (currentTrackIndex >= playlistManager.getPlaylist().size()) {
            currentTrackIndex = 0;
        }
        playMusic();
    }

    private void playPrevious() {
        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = playlistManager.getPlaylist().size() - 1;
        }
        playMusic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
