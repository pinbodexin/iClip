package com.chinaso.demo.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chinaso.demo.R;
import com.chinaso.demo.entity.Song;

import java.util.List;

/**
 * @author: ji xin
 * @date : 2019/9/5上午10:06
 * @desc :
 */
public class LocalMusicAdapter extends BaseQuickAdapter<Song,BaseViewHolder> {
    public LocalMusicAdapter(int layoutResId, @Nullable List<Song> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Song item) {
        helper.setText(R.id.tv_music_info,item.getSong()+"---"+item.getSinger());
    }
}
