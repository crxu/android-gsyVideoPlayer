package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.ui.base.BaseDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;
import hdpfans.com.R;

/**
 * 重置对话框
 */
public class ResetDialog extends BaseDialog {

    public ResetDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_reset;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = 320;
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();
    }

    @OnClick(R.id.btn_ok)
    void onOkClick() {
        EventBus.getDefault().post(new PlayEvent.ResetEvent());
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    void onCancelClick() {
        dismiss();
    }

}
