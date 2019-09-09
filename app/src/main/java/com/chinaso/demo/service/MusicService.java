package com.chinaso.demo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.chinaso.demo.entity.MyMessage;
import com.chinaso.demo.entity.Song;

/**
 * @author: ji xin
 * @date : 2019/9/4下午3:55
 * @desc :
 */
public class MusicService extends Service {
    private String TAG = MusicService.class.getSimpleName();
    /**
     * 重复播放次数
     */
    private final int REPEAT_TIMES = 10000;
    private MediaPlayer mediaPlayer;
    private Messenger serviceMessenger;
    private Messenger activityMessenger;
    private int musicLength;
    private boolean keepTrue = true;
    private Song currentSong;
    private CountDownTimer countDownTimer;
    private int minPosition;
    private int maxPosition;
    private boolean isPause = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://播放音乐
                    cancelDownTimer();
                    MyMessage myMessage = (MyMessage) msg.obj;
                    //如果换了一首新歌，isPause字段恢复默认
                    if (currentSong != null) {
                        if (!myMessage.getSong().getPath().equals(currentSong.getPath())) {
                            isPause = false;
                        }
                    }
                    if (currentSong != null) {
                        Log.i(TAG, "myMessage:" + myMessage.getSong().getSong() + "," + currentSong.getSong());
                    }
                    currentSong = myMessage.getSong();
                    if (!isPause) {
                        prepareMusic();
                        playMusic();
                    } else {
                        //继续播放当前用户选中的播放位置、起始点
                        minPosition = myMessage.getStartPosition();
                        maxPosition = myMessage.getEndPosition();
                        repeatAToB(myMessage.getCurrentPosition(), REPEAT_TIMES);
                    }
                    updateSeekBar();
                    break;
                case 2://暂停播放
                    isPause = true;
                    pauseMusic();
                    cancelDownTimer();
                    break;
                case 3://循环播放一段音乐
                    minPosition = msg.arg1;
                    maxPosition = msg.arg2;
                    repeatAToB(-1, REPEAT_TIMES);
                    break;
                case 4://播放指定位置
                    int currentPosition = msg.arg1;
                    minPosition = (int) msg.obj;
                    maxPosition = msg.arg2;
                    repeatAToB(currentPosition, REPEAT_TIMES);
                    break;
                case 999:
                    activityMessenger = msg.replyTo;
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        serviceMessenger = new Messenger(mHandler);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void repeatAToB(final int currentPosition, final int repeatTimes) {
        cancelDownTimer();
        int millisInFuture;
        if (currentPosition == -1) {
            mediaPlayer.seekTo(minPosition);
            millisInFuture = maxPosition - minPosition;
        } else {
            mediaPlayer.seekTo(currentPosition);
            millisInFuture = maxPosition - currentPosition;
        }
        mediaPlayer.start();
        Log.e(TAG, "repeatAToB()-----startPos," + minPosition + ",endPos," + maxPosition);

        countDownTimer = new CountDownTimer(/* millisInFuture= */millisInFuture, /* countDownInterval= */1000) {
            public void onTick(long millisUntilFinished) {
//                Log.i(TAG, "onTick");
            }

            public void onFinish() {
                Log.i(TAG, "repeatAToB()---onFinish");
                if ((repeatTimes - 1) > 0) {
                    try {
                        repeatAToB(-1, repeatTimes - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mediaPlayer.stop();
                }
            }
        };
        countDownTimer.start();
    }

    private void cancelDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    /**
     * 准备播放音乐
     */
    private void prepareMusic() {
        Log.i(TAG,"prepareMusic()");
        try {
            destoryMediaPlayer();
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.setLooping(true);
            //prepare()同步的方式装载资源，可能会造成UI界面的卡顿
            mediaPlayer.prepare();
            musicLength = mediaPlayer.getDuration();
            maxPosition = musicLength;
            Message obtain = Message.obtain();
            obtain.what = 2;
            obtain.arg1 = musicLength;
            sendMessage(obtain);

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.i(TAG, "mediaPlayer--onError," + extra + "," + what);
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "出现异常--" + e.toString());
        }
    }

    public void destoryMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
        }
    }
    /**
     * 播放音乐
     */
    private void playMusic() {
        Log.i(TAG,"playMusic()");
        try {
            mediaPlayer.start();
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> {
                Log.i(TAG, "播放完成！！！");
                //播放完成，发送数据给activity
                Message obtain = Message.obtain();
                obtain.what = 3;
                sendMessage(obtain);
                prepareMusic();
                cancelDownTimer();
                repeatAToB(-1, REPEAT_TIMES);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "出现异常，" + e.toString());
        }
    }

    /**
     * 暂停播放音乐
     */
    private void pauseMusic() {
        Log.i(TAG,"pauseMusic()");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 继续播放音乐
     */
    private void continueMusic() {
        mediaPlayer.start();
    }

    /**
     * 播放指定位置
     *
     * @param position
     */
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    /**
     * 更新SeekBar
     */
    private void updateSeekBar() {
        //开启线程发送数据
        Thread musicThread = new Thread(() -> {
            while (keepTrue) {
                if (musicLength > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    Message obtain = Message.obtain();
                    obtain.what = 1;
                    obtain.arg1 = currentPosition;
                    obtain.arg2 = musicLength;
                    sendMessage(obtain);
                }
            }
        });
        musicThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     * 发送消息给Activity
     *
     * @param message
     */
    private void sendMessage(Message message) {
        try {
            activityMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "sendMessage," + message.what + "," + e.toString());
        }
    }
}
