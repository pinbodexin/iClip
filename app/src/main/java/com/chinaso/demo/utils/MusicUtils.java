package com.chinaso.demo.utils;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.chinaso.demo.entity.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 音乐工具类,
 */
public class MusicUtils {
    private static String TAG=MusicUtils.class.getSimpleName();
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                Log.i(TAG,"singer:"+song.singer);
                if(song.singer.contains("<unknown>")){
                    Log.i(TAG,"-----没有歌星名称");
                    song.singer="";
                }
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String[] split = song.path.split("/");
                if (split.length > 0) {
                    song.song = split[split.length - 1];
                    Log.i(TAG,"------song:"+song.song);
                } else {
                    song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).trim();
                }
                song.setPinyin(PinYinUtils.getPinyin(song.song.trim()));
                Log.i(TAG,"----PinYin:"+song.getPinyin());
                song.setHeaderWord(song.getPinyin().substring(0, 1));

                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                song.isChecked=false;
//                if (song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.song.contains("-")&&!Tools.isEmptyString(song.singer)&&!song.singer.contains("<unknown>")) {
                        String[] str = song.song.split("-");
                        song.singer = str[0];
                        song.song = str[1].trim();
                        song.setPinyin(PinYinUtils.getPinyin(song.song.trim()));
                        song.setHeaderWord(song.getPinyin().substring(0, 1));
                    }
                    list.add(song);
//                }
            }
            // 释放资源
            cursor.close();
        }
        Collections.sort(list);
        Log.i(TAG,"list:"+list.size());
        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}

