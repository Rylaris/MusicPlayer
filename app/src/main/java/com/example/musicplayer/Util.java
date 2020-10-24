package com.example.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;


public class Util {
    public static List<MyMusic> getMusicDate(Context context) {
        List<MyMusic> musics = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor != null && cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            if (author.equals("<unknown")) {
                author = "未知艺术家";
            }

            if (duration > 20000) {
                MyMusic myMusic = new MyMusic(name, author, path, duration);
                musics.add(myMusic);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return musics;
    }

    public static boolean checkPublishPermission(Activity activity) {
        List<String> permissions = new ArrayList<>();
        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissions.size() != 0) {
            ActivityCompat.requestPermissions(activity, (String[])permissions.toArray(),
                    MainActivity.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }
}
