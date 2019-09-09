package com.chinaso.demo.entity;

import android.support.annotation.NonNull;

/**
 * 搜索本地歌曲，歌曲信息bean
 * Created by jixin on 2018/7/4.
 */

public class Song   implements   Comparable<Song> {
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long id;

    /**
     * 歌手
     */
    public String singer;
    /**
     * 歌曲名
     */
    public String song;
    /**
     * 歌曲的地址
     */
    public String path;
    /**
     * 歌曲长度
     */
    public int duration;
    /**
     * 歌曲的大小
     */
    public long size;

    /**
     * 是否选中
     */
    public boolean isChecked;


    //拼音
    private String pinyin;
    //拼音首字母
    private String headerWord;

    public boolean isItemSelected() {
        return isItemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        isItemSelected = itemSelected;
    }

    /**
     * 当前的条目是否选中，用来更新当前音乐图标的
     */
    private  boolean isItemSelected=false;

    public boolean isFamilyVoice() {
        return isFamilyVoice;
    }

    public void setFamilyVoice(boolean familyVoice) {
        isFamilyVoice = familyVoice;
    }

    /**
     * 判断是否是语音，此字段是用来区分是音乐还是语音的，false表示是音乐，true表示是语音
     */
    private boolean isFamilyVoice=false;

    public Song(String singer, String song, String path, int duration, long size) {
        this.singer = singer;
        this.song = song;
        this.path = path;
        this.duration = duration;
        this.size = size;
    }

    public Song() {
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {

        this.size = size;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {

        this.pinyin = pinyin;
    }

    public String getHeaderWord() {
        return headerWord;
    }

    public void setHeaderWord(String headerWord) {

        if (!headerWord.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            this.headerWord = "#";
        } else {

            this.headerWord = headerWord;
        }

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    @Override
    public String toString() {
        return "Song{" +
                "singer='" + singer + '\'' +
                ", song='" + song + '\'' +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", pinyin='" + pinyin + '\'' +
                ", headerWord='" + headerWord + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Song song) {
        if (headerWord.equals("#") && !song.getHeaderWord().equals("#")) {
            return 1;
        } else if (!headerWord.equals("#") && song.getHeaderWord().equals("#")) {
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(song.getPinyin());
        }
    }
}
