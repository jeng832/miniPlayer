package com.tweak.just.miniplayer.ui.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.tweak.just.miniplayer.R;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private PlayerViewModel playerViewModel;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvRemainingTime;
    private ImageView imgAlbumArt;
    private SeekBar seekBar;
    private ImageButton btnPlayPause, btnNext, btnPrev;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // View 초기화
        imgAlbumArt = findViewById(R.id.img_album_art);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvRemainingTime = findViewById(R.id.tv_remaining_time);
        seekBar = findViewById(R.id.seek_bar);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_prev);

        // ViewModel 초기화
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        // Intent로 전달된 데이터 받기
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String url = intent.getStringExtra("url");

        // ViewModel에 데이터 설정
        playerViewModel.updateMusicInfo(title, artist);
        playerViewModel.initializePlayer(url);

        // LiveData 관찰
        playerViewModel.getSongTitle().observe(this, tvTitle::setText);
        playerViewModel.getSongArtist().observe(this, tvArtist::setText);
        playerViewModel.getAlbumArt().observe(this, bitmap -> {
            if (bitmap != null) {
                imgAlbumArt.setImageBitmap(bitmap);
            } else {
                imgAlbumArt.setImageResource(R.drawable.ic_music_placeholder);
            }
        });
        playerViewModel.getCurrentPosition().observe(this, position -> {
            seekBar.setProgress(position);
            tvCurrentTime.setText(formatTime(position));
        });
        playerViewModel.getDuration().observe(this, seekBar::setMax);
        playerViewModel.getIsPlaying().observe(this, isPlaying ->
                btnPlayPause.setImageResource(isPlaying ?
                        android.R.drawable.ic_media_pause :
                        android.R.drawable.ic_media_play)
        );

        // 버튼 리스너
        btnPlayPause.setOnClickListener(v -> playerViewModel.togglePlayPause());
        btnNext.setOnClickListener(v -> playerViewModel.updateMusicInfo("다음 곡", "다음 아티스트"));
        btnPrev.setOnClickListener(v -> playerViewModel.updateMusicInfo("이전 곡", "이전 아티스트"));
    }

    private String formatTime(int millis) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }
}
