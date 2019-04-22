package com.haikan.sport.musicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.haikan.sport.musicplayer.music.SongBean;

import java.util.ArrayList;

/**
 * Created by admin on 2019/4/17.
 * <p>Copyright 2019 SDGD.</p>
 */
public class ScanUtils {

    public static ArrayList<SongBean> getAllSongs(Context context) {

        ArrayList<SongBean> songs = new ArrayList<>();
        //Cursor是每行的集合,context.getContentResolver();查询媒体数据库
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.DURATION,
                        //歌曲的歌手名MediaStore.Audio.AudioColumns.ARTIST
                        MediaStore.Audio.AudioColumns.ARTIST,
                        //歌曲的专辑名MediaStore.Audio.AudioColumns.ALBUM
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.YEAR,
                        MediaStore.Audio.AudioColumns.MIME_TYPE,
                        MediaStore.Audio.AudioColumns.SIZE,
                        //歌曲文件的路径MediaStore.Audio.AudioColumns.DATA
                        MediaStore.Audio.AudioColumns.DATA},
                MediaStore.Audio.AudioColumns.MIME_TYPE + "=? or " + MediaStore.Audio.AudioColumns.MIME_TYPE + "=?", new String[]{"audio/mpeg", "audio/x-ms-wma"}, null);
        if (cursor.moveToFirst()) {
            do {
                SongBean song = new SongBean();
                // 文件名
                song.setFileName(cursor.getString(1));
                // 歌曲名
                song.setTitle(cursor.getString(2));
                // 时长
                Log.i("MARK", "Duration: "+cursor.getInt(3));
                song.setDuration(cursor.getInt(3));
                // 歌手名
                song.setSinger(cursor.getString(4));
                // 专辑名
                song.setAlbum(cursor.getString(5));
                // 专辑id
                song.setAlbumId(cursor.getLong(6));
                // 年代
                if (cursor.getString(7) != null) {
                    song.setYear(cursor.getString(7));
                } else {
                    song.setYear("未知");
                }
                // 歌曲格式
                if(cursor.getString(7)!=null){
                    if ("audio/mpeg".equals(cursor.getString(8).trim())) {
                        song.setType("mp3");
                    } else if ("audio/x-ms-wma".equals(cursor.getString(8).trim())) {
                        song.setType("wma");
                    }
                }

                // 文件大小
                if (cursor.getString(9) != null) {
                    float size = cursor.getInt(9) / 1024f / 1024f;
                    song.setSize((size + "").substring(0, 4) + "M");
                } else {
                    song.setSize("未知");
                }
                // 文件路径
                if (cursor.getString(10) != null) {
                    song.setFileUrl(cursor.getString(10));
                }
                songs.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }
}


