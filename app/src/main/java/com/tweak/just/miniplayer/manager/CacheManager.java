package com.tweak.just.miniplayer.manager;

import android.content.Context;
import com.tweak.just.miniplayer.model.Music;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class CacheManager {

    private static final long MAX_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private Context context;

    public CacheManager(Context context) {
        this.context = context;
    }

    public String getCachedFilePath(Music music) {
        File cacheDir = context.getCacheDir();
        File cachedFile = new File(cacheDir, "music_" + music.getId() + ".mp3");
        return cachedFile.exists() ? cachedFile.getAbsolutePath() : null;
    }

    public void cacheMusicFile(Music music) {
        if (getCachedFilePath(music) != null) return;

        try {
            URL url = new URL(music.getPath());
            InputStream inputStream = url.openStream();
            File cacheFile = new File(context.getCacheDir(), "music_" + music.getId() + ".mp3");
            FileOutputStream fos = new FileOutputStream(cacheFile);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }

            fos.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
