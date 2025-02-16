package com.tweak.just.miniplayer.repository;

import android.content.Context;

public class DriveMusicRepository {
    private Context context;

    public DriveMusicRepository(Context context) {
        this.context = context;
    }

    public void fetchDriveMusicList() {
        // 구글 드라이브 API를 통해 음악 파일 목록 가져오기
        // OAuth 인증 처리 필요
    }
}
