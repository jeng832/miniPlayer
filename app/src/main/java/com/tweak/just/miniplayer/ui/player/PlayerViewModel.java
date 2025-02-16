package com.tweak.just.miniplayer.ui.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlayerViewModel extends ViewModel {

    // 현재 재생 중인 음악 정보
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> artist = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> duration = new MutableLiveData<>(0);

    private MediaPlayer mediaPlayer;

    // 음악 정보 가져오기
    public LiveData<String> getTitle() {
        return title;
    }

    public LiveData<String> getArtist() {
        return artist;
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public LiveData<Integer> getDuration() {
        return duration;
    }

    // 미디어플레이어 초기화
    public void initializePlayer(String dataSource) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            // MediaPlayer 초기화 시 AudioAttributes 적용
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            mediaPlayer.setAudioAttributes(audioAttributes);
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                duration.setValue(mp.getDuration());
                playMusic();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying.setValue(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 재생
    public void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying.setValue(true);
            startUpdatingPosition();
        }
    }

    // 일시 정지
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying.setValue(false);
        }
    }

    // 재생/중지 토글
    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                pauseMusic();
            } else {
                playMusic();
            }
        }
    }

    // 재생 위치 업데이트
    private void startUpdatingPosition() {
        new Thread(() -> {
            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition.postValue(mediaPlayer.getCurrentPosition());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 플레이어 해제
    @Override
    protected void onCleared() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // 제목, 아티스트 업데이트
    public void updateMusicInfo(String title, String artist) {
        this.title.setValue(title);
        this.artist.setValue(artist);
    }
}
