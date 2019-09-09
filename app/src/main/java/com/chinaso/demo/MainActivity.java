package com.chinaso.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chinaso.demo.base.BaseActivity;
import com.chinaso.demo.entity.MyMessage;
import com.chinaso.demo.entity.Song;
import com.chinaso.demo.service.MusicService;
import com.chinaso.demo.ui.ChooseMusicActivity;
import com.chinaso.demo.utils.RxEvent;
import com.chinaso.demo.utils.ToastUtil;
import com.chinaso.demo.widget.CustomSeekBar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.btn_choose_music)
    Button btnChooseMusic;
    @BindView(R.id.seek_bar)
    CustomSeekBar mySeekBar;
    @BindView(R.id.seek_bar_start)
    CustomSeekBar mySeekBarStart;
    @BindView(R.id.seek_bar_end)
    CustomSeekBar mySeekBarEnd;
    @BindView(R.id.btn_choose_current_start)
    Button btnChooseCurrentStart;
    @BindView(R.id.btn_choose_current_end)
    Button btnChooseCurrentEnd;
    @BindView(R.id.ll_choose)
    LinearLayout llChoose;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.btn_ll_trimming_start_left)
    Button btnLlTrimmingStartLeft;
    @BindView(R.id.btn_ll_trimming_start_right)
    Button btnLlTrimmingStartRight;
    @BindView(R.id.ll_trimming_start)
    LinearLayout llTrimmingStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.btn_ll_trimming_end_left)
    Button btnLlTrimmingEndLeft;
    @BindView(R.id.btn_ll_trimming_end_right)
    Button btnLlTrimmingEndRight;
    @BindView(R.id.ll_trimming_end)
    LinearLayout llTrimmingEnd;
    private String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.main_tv_music_name)
    TextView mainTvMusicName;
    @BindView(R.id.btn_play)
    CheckBox ckPlay;
    private Messenger serviceMessenger;
    private Messenger activityMessenger;
    private Song currentSong;
    private int totalDuration;
    private int startPosition = 0;
    private int endPosition = totalDuration;
    /**
     * 起点和终点的最小间距差
     */
    private int minDistance = 10 * 1000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://更新进度条
                    int currentPosition = msg.arg1;
                    totalDuration = msg.arg2;
//                    Log.i(TAG, "currentPosition," + currentPosition + "," + totalDuration);
                    mySeekBar.setProgress(currentPosition);
                    break;
                case 2://进度条总时长
                    totalDuration = msg.arg1;
                    Log.i(TAG, "totalDuration," + totalDuration);
                    mySeekBar.setMax(totalDuration);
                    mySeekBarStart.setMax(totalDuration);
                    mySeekBarEnd.setMax(totalDuration);
                    mySeekBarStart.setProgress(0);
                    mySeekBarEnd.setProgress(totalDuration);
                    mySeekBar.setTouchingProgressBar(true);
                    mySeekBarStart.setTouchingProgressBar(true);
                    mySeekBarEnd.setTouchingProgressBar(true);
                    break;
                case 3://播放完成
                    new ToastUtil(MainActivity.this).showToast("播放完成！");
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        requestPermission();
        initService();
    }

    private void initService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceMessenger = new Messenger(service);
                activityMessenger = new Messenger(mHandler);
                Message message = Message.obtain();
                message.what = 999;
                message.replyTo = activityMessenger;
                sendMessage(message);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        ckPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (checkNull()) return;
                ckPlay.setText("暂停播放");
                playMusic();
            } else {
                if (checkNull()) return;
                ckPlay.setText("继续播放");
                pauseMusic();
            }
        });
        mySeekBar.setTouchingProgressBar(false);
        mySeekBarStart.setTouchingProgressBar(false);
        mySeekBarEnd.setTouchingProgressBar(false);
        addSeekBarListener();
    }

    private void addSeekBarListener() {
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > mySeekBarEnd.getProgress()) {
                    mySeekBar.setProgress(mySeekBarEnd.getProgress());
                } else if (progress < mySeekBarStart.getProgress()) {
                    mySeekBar.setProgress(mySeekBarStart.getProgress());
                } else {
                    mySeekBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ckPlay.setChecked(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ckPlay.setChecked(true);
//                Message obtain = Message.obtain();
//                obtain.what = 4;
//                obtain.arg1 = mySeekBar.getProgress();
//                obtain.obj = mySeekBarStart.getProgress();
//                obtain.arg2 = mySeekBarEnd.getProgress();
//                try {
//                    serviceMessenger.send(obtain);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }

            }
        });
        mySeekBarStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "mySeekBarStart-onProgressChanged");
                if (progress > endPosition) {
                    mySeekBarStart.setProgress((endPosition - minDistance));
                } else {
                    startPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "mySeekBarStart-onStartTrackingTouch");
                ckPlay.setChecked(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mySeekBarEnd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < startPosition) {
                    mySeekBarEnd.setProgress((startPosition + minDistance));
                } else {
                    endPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ckPlay.setChecked(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 检查歌曲是否为null
     *
     * @return
     */
    private boolean checkNull() {
        if (currentSong == null) {
            new ToastUtil(MainActivity.this).showToast("请先选择音乐");
            return true;
        }
        return false;
    }

    private void playMusic() {
        Message message = Message.obtain();
        message.what = 1;
        MyMessage myMessage = new MyMessage();
        myMessage.setSong(currentSong);
        myMessage.setCurrentPosition(mySeekBar.getProgress());
        myMessage.setStartPosition(mySeekBarStart.getProgress());
        myMessage.setEndPosition(mySeekBarEnd.getProgress());
        message.obj = myMessage;
        sendMessage(message);

    }


    private void pauseMusic() {
        Message message2 = Message.obtain();
        message2.what = 2;
        sendMessage(message2);
    }

    /**
     * 给service发送消息
     *
     * @param message
     */
    private void sendMessage(Message message) {
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_choose_music, R.id.btn_choose_current_start, R.id.btn_choose_current_end, R.id.btn_ll_trimming_start_left, R.id.btn_ll_trimming_start_right, R.id.btn_ll_trimming_end_left, R.id.btn_ll_trimming_end_right})
    public void onViewClicked(View view) {
        int max = 0;
        int progress = 0;
        int trimming = 0;
        switch (view.getId()) {
            case R.id.btn_choose_music:
                Intent intent = new Intent(MainActivity.this, ChooseMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_choose_current_start://设置当前播放为起点
                if (checkNull()) return;
                startPosition = mySeekBar.getProgress();
                mySeekBarStart.setProgress(startPosition);
//                Message obtain2 = Message.obtain();
//                obtain2.what = 3;
//                obtain2.arg1 = mySeekBar.getProgress();
//                obtain2.arg2 = mySeekBarEnd.getProgress();
//                sendMessage(obtain2);
                ckPlay.setChecked(false);
                break;
            case R.id.btn_choose_current_end://设置当前播放为终点
                if (checkNull()) return;
                endPosition = mySeekBar.getProgress();
                mySeekBarEnd.setProgress(endPosition);
//                Message obtain3 = Message.obtain();
//                obtain3.what = 3;
//                obtain3.arg1 = mySeekBarStart.getProgress();
//                obtain3.arg2 = mySeekBar.getProgress();
//                sendMessage(obtain3);
                ckPlay.setChecked(false);
                break;
            case R.id.btn_ll_trimming_start_left://起点向左微调
                if (checkNull()) return;
                ckPlay.setChecked(false);
                progress = mySeekBarStart.getProgress();
                max = mySeekBarStart.getMax();
                trimming = (int) (max * TRIMMING_FACTOR);
                Log.i(TAG, "max:" + max + "," + trimming + "," + progress + "," + (progress - trimming));
                if ((progress - trimming) < 0) {
                    mySeekBarStart.setProgress(0);
                } else {
                    mySeekBarStart.setProgress(progress - trimming);
                }
                break;
            case R.id.btn_ll_trimming_start_right://起点向右微调
                if (checkNull()) return;
                ckPlay.setChecked(false);
                progress = mySeekBarStart.getProgress();
                max = mySeekBarEnd.getMax();
                trimming = (int) (max * TRIMMING_FACTOR);
                Log.i(TAG, "max:" + max + "," + trimming + "," + progress + "," + (progress - trimming));
                if ((progress + trimming) > (endPosition - minDistance)) {
                    Log.i(TAG, "dayu");
                    mySeekBarStart.setProgress(endPosition - minDistance);
                } else {
                    Log.i(TAG, "-======");
                    mySeekBarStart.setProgress(progress + trimming);
                }
                break;
            case R.id.btn_ll_trimming_end_left://终点向左微调
                if (checkNull()) return;
                ckPlay.setChecked(false);
                progress = mySeekBarEnd.getProgress();
                trimming = (int) (mySeekBarEnd.getMax() * TRIMMING_FACTOR);
                Log.i(TAG, "max:" + max + "," + trimming + "," + progress + "," + (progress - trimming));
                if ((progress - trimming) < (startPosition + minDistance)) {
                    mySeekBarEnd.setProgress(startPosition + minDistance);
                } else {
                    mySeekBarEnd.setProgress(progress - trimming);
                }
                break;
            case R.id.btn_ll_trimming_end_right://终点向右微调
                if (checkNull()) return;
                ckPlay.setChecked(false);
                progress = mySeekBarEnd.getProgress();
                max = mySeekBarEnd.getMax();
                trimming = (int) (max * TRIMMING_FACTOR);
                Log.i(TAG, "max:" + max + "," + trimming + "," + progress + "," + (progress + trimming));
                if ((progress + trimming) > max) {
                    mySeekBarEnd.setProgress(max);
                } else {
                    mySeekBarEnd.setProgress(progress + trimming);
                }
                break;
        }
    }

    /**
     * 微调因子
     */
    private float TRIMMING_FACTOR = 0.015f;

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        new ToastUtil(this).showToast("没有存储权限，不能播放音乐");
                    }
                });
    }

    private void test() {
        if (checkNull()) return;
        Message obtain = Message.obtain();
        obtain.what = 3;
        obtain.arg1 = mySeekBarStart.getProgress();
        obtain.arg2 = mySeekBarEnd.getProgress();
        sendMessage(obtain);
    }

    @Override
    protected void onEvent(RxEvent event) {
        super.onEvent(event);
        switch (event.getCode()) {
            case 1233:
                currentSong = (Song) event.getData();
                mainTvMusicName.setText("当前音乐：" + currentSong.getSong());
                mySeekBar.setProgress(0);
                playMusic();
                ckPlay.setChecked(true);
                break;
        }
    }

}
