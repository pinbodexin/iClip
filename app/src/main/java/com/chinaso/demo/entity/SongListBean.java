package com.chinaso.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class SongListBean implements Parcelable, Comparable<SongListBean> {

    public long music_id;
    public String music_local_path;
    public String musicName;


    private String musicUrl="";
    //拼音
    private String pinyin;
    //拼音首字母
    private String headerWord;




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

    public SongListBean() {
    }

    public SongListBean(long music_id, String musicName, String music_local_path) {
        this.music_id = music_id;
        this.music_local_path = music_local_path;
        this.musicName = musicName;
    }

    protected SongListBean(Parcel in) {

        music_id = in.readLong();
        music_local_path = in.readString();
        musicName = in.readString();
    }
    public long getMusic_id() {
        return music_id;
    }

    public void setMusic_id(long music_id) {
        this.music_id = music_id;
    }

    public String getMusic_local_path() {
        return music_local_path;
    }

    public void setMusic_local_path(String music_local_path) {
        this.music_local_path = music_local_path;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }



    public static final Creator<SongListBean> CREATOR = new Creator<SongListBean>() {
        @Override
        public SongListBean createFromParcel(Parcel in) {

            return new SongListBean(in);
        }

        @Override
        public SongListBean[] newArray(int size) {
            return new SongListBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(music_id);
        dest.writeString(music_local_path);
        dest.writeString(musicName);

    }

    @Override
    public int compareTo(@NonNull SongListBean bean) {
        if (headerWord.equals("#") && !bean.getHeaderWord().equals("#")) {
            return 1;
        } else if (!headerWord.equals("#") && bean.getHeaderWord().equals("#")) {
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(bean.getPinyin());
        }
    }
    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

}