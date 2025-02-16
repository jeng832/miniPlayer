package com.tweak.just.miniplayer.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tweak.just.miniplayer.R;
import com.tweak.just.miniplayer.ui.player.PlayerActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private Button btnOpenPlayer;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 런타임 권한 요청
        requestPermissions();

        btnOpenPlayer = findViewById(R.id.btn_open_player);
        btnOpenPlayer.setOnClickListener(v -> {
            String displayName = "sample_song.mp3"; // Music 폴더에 있는 파일명
            String musicPath = getAudioFilePath(displayName);

            if (musicPath == null) {
                Log.e(TAG, "MediaStore를 통해 파일을 찾지 못함. 기본 경로 시도.");
                musicPath = "/storage/emulated/0/Music/sample_song.mp3"; // 기본 경로
            }

            // 파일 존재 여부 확인
            File file = new File(musicPath);
            if (file.exists()) {
                Log.d(TAG, "음악 파일 찾음: " + musicPath);
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("title", "Sample Song");
                intent.putExtra("artist", "Unknown Artist");
                intent.putExtra("url", "file://" + file.getAbsolutePath()); // file:// 스킴 추가
                startActivity(intent);
            } else {
                Log.e(TAG, "음악 파일이 존재하지 않음: " + musicPath);
            }
        });
    }

    /**
     * 필수 권한 요청
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_AUDIO
            };

            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    break;
                }
            }
        }
    }

    /**
     * MediaStore를 통해 오디오 파일 경로를 가져오는 메서드 (Android 10 이상 대응)
     */
    private String getAudioFilePath(String displayName) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{displayName};

        try (Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "MediaStore 조회 중 오류 발생: " + e.getMessage());
        }
        return null;
    }
}
