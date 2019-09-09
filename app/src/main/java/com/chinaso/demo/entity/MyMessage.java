package com.chinaso.demo.entity;

/**
 * @author: ji xin
 * @date : 2019/9/6上午10:40
 * @desc :
 */
public class MyMessage {
    private Song song;
    private int currentPosition;
    private int startPosition;
    private int endPosition;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }
}
