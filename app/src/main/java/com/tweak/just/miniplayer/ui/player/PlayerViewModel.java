package com.tweak.just.miniplayer.ui.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import java.io.IOException;

public class PlayerViewModel extends ViewModel {

    // 현재 재생 중인 음악 정보
    private final MutableLiveData<String> songTitle = new MutableLiveData<>();
    private final MutableLiveData<String> songArtist = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> albumArt = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> duration = new MutableLiveData<>(0);

    private MediaPlayer mediaPlayer;

    // 음악 정보 가져오기
    public LiveData<String> getSongTitle() {
        return songTitle;
    }

    public LiveData<String> getSongArtist() {
        return songArtist;
    }

    public LiveData<Bitmap> getAlbumArt() {
        return albumArt;
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

        loadMetadata(dataSource);

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
        this.songTitle.setValue(title);
        this.songArtist.setValue(artist);
    }

    /**
     * MediaMetadataRetriever를 사용해 MP3 태그 추출
     */
    private void loadMetadata(String dataSource) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(dataSource);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                albumArt.postValue(bitmap);
            } else {
                albumArt.postValue(null); // 앨범 아트 없음
            }

            songTitle.postValue(title != null ? title : "Unknown Title");
            songArtist.postValue(artist != null ? artist : "Unknown Artist");

        } catch (Exception e) {
            e.printStackTrace();
            songTitle.postValue("Unknown Title");
            songArtist.postValue("Unknown Artist");
            albumArt.postValue(null);
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
