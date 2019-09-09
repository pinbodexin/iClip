package com.chinaso.demo.ui;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chinaso.demo.R;
import com.chinaso.demo.base.BaseActivity;
import com.chinaso.demo.entity.Song;
import com.chinaso.demo.ui.adapter.LocalMusicAdapter;
import com.chinaso.demo.utils.MusicUtils;
import com.chinaso.demo.utils.RxBus;
import com.chinaso.demo.utils.RxEvent;
import com.chinaso.demo.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author: ji xin
 * @date : 2019/9/5上午9:58
 * @desc :
 */
public class ChooseMusicActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.rv_music_list)
    RecyclerView rvMusicList;
    private LocalMusicAdapter adapter;
    private List<Song> musicDataList = new ArrayList<>();

    @Override
    protected void initData() {
    }

    @Override
    protected int getViewLayout() {
        return R.layout.activity_choose_music;
    }

    @Override
    protected void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMusicList.setLayoutManager(linearLayoutManager);
        adapter = new LocalMusicAdapter(R.layout.item_local_music, musicDataList);
        rvMusicList.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            new ToastUtil(this).showToast("开始播放--"+musicDataList.get(position).getSong());
            RxBus.getDefault().post(new RxEvent<>(1233, musicDataList.get(position)));
            finish();
        });
        requestPermission();
    }

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        searchLocalMusic();
                    } else {
                        new ToastUtil(this).showToast("读取本地音乐文件需要存储权限！");
                    }
                });
    }

    /**
     * 搜索本地音乐
     */
    private void searchLocalMusic() {
        new Thread(){
            @Override
            public void run() {
                musicDataList.addAll(MusicUtils.getMusicData(ChooseMusicActivity.this));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

    }
}
