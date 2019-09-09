package com.chinaso.demo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.chinaso.demo.utils.RxBus;
import com.chinaso.demo.utils.RxEvent;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author: ji xin
 * @date : 2019/9/5上午9:51
 * @desc :
 */
public abstract class BaseActivity extends FragmentActivity {
    private CompositeDisposable mDisposables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册eventbus
        Disposable disposable = RxBus.getDefault()
                .register(RxEvent.class, event -> {
                    int eventCode = event.getCode();
                    Log.e("RxBus", event.toString());
                    switch (eventCode) {
                        case RxEvent.EVENT_CLOSE_ALL_ACTIVITY:
                            break;
                        default:
                            onEvent(event);
                            break;
                    }
                });
        addDispose(disposable);
        setContentView(getViewLayout());
        ButterKnife.bind(this);
        initData();
        initView();
    }
    protected abstract void initData();
    protected abstract int getViewLayout();
    protected abstract void initView();
    protected  void onEvent(RxEvent event){

    };
    /**
     * RxJava 添加订阅
     */
    protected void addDispose(Disposable disposable) {
        if (mDisposables == null) {
            mDisposables = new CompositeDisposable();
        }
        //将所有disposable放入,集中处理
        mDisposables.add(disposable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposables != null) {
            RxBus.getDefault().unregister(mDisposables);
            mDisposables.clear();
            mDisposables = null;
        }
    }
}
