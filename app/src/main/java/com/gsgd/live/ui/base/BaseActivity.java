package com.gsgd.live.ui.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gsgd.live.ui.dialog.WaitDialog;
import com.jiongbull.jlog.JLog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import hdpfans.com.R;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable;
    protected Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        doOnCreate(savedInstanceState);
        setContentView(getLayoutResId());

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initParams();
        initEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearDisposable();
        EventBus.getDefault().unregister(this);
    }

    public void addDisposable(Disposable disposable) {
        if (null == disposable) {
            return;
        }

        try {
            if (null == mCompositeDisposable) {
                mCompositeDisposable = new CompositeDisposable();
            }
            mCompositeDisposable.add(disposable);

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    public void clearDisposable() {
        try {
            if (null != mCompositeDisposable) {
                mCompositeDisposable.clear();
            }

        } catch (Exception e) {
            JLog.e(e);
        }
    }

    protected void doOnCreate(Bundle savedInstanceState) {
        applyTranslucency();
    }

    /**
     * 设置状态栏透明
     */
    protected void applyTranslucency() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected abstract int getLayoutResId();

    protected void initParams() {
    }

    protected void initEvents() {
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmptyEvent(Void v) {
    }

    //等待框
    private WaitDialog dialog;

    protected void waiting(String msg, boolean cancelable) {
        try {
            dialog = new WaitDialog(mContext, R.style.TransparentDialog, msg);
            dialog.show();
        } catch (Exception e) {
            JLog.e(e);
        }
    }

    protected void waiting(String msg) {
        stopWaiting();
        waiting(msg, true);
    }

    protected void stopWaiting() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            JLog.e(e);
        }
    }

}
