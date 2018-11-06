package com.yidu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author andy on 2018/1/23
 *         默认的升级提示框
 */
class UpdateHintDialog extends Dialog {

    private Context mContext;
    private String mFilePath;
    private boolean isForceUpgrade;
    private UpdateManager.OnUpdateListener mOnUpdateListener;

    public void setOnUpdateListener(UpdateManager.OnUpdateListener listener) {
        this.mOnUpdateListener = listener;
    }

    protected UpdateHintDialog(Context context, String filePath, boolean isForceUpgrade) {
        super(context, R.style.UpdateHintTransparentDialog);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("context is not activity");
        }
        this.isForceUpgrade = isForceUpgrade;
        this.mContext = context;
        this.mFilePath = filePath;
        if (TextUtils.isEmpty(this.mFilePath)) {
            throw new IllegalArgumentException("file path is null");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_update_hint, null);
        setContentView(layout);
        setCanceledOnTouchOutside(!isForceUpgrade);
        setCancelable(!isForceUpgrade);
        getWindow().setGravity(Gravity.CENTER);
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnUpdateListener != null) {
                    mOnUpdateListener.onUpdate(isForceUpgrade);
                }
                UpdateManager.install(mContext, mFilePath);
                dismiss();
            }
        });
    }
}
